package com.labourwage.labourwagesystem.service;

import com.labourwage.labourwagesystem.dto.response.WageSlipResponse;
import com.labourwage.labourwagesystem.entity.*;
import com.labourwage.labourwagesystem.enums.AttendanceTypeEnum;
import com.labourwage.labourwagesystem.repository.*;
import com.labourwage.labourwagesystem.util.WageSlipPdfUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WageCalculationService {

    private final AttendanceRepository  attendanceRepository;
    private final WageSlipRepository    wageSlipRepository;
    private final WorkerRepository      workerRepository;
    private final SiteRepository        siteRepository;
    private final MinWageRuleRepository minWageRuleRepository;
    private final WageSlipPdfUtil       pdfUtil;

    @Value("${file.upload.dir}")
    private String uploadDir;

    // PF and ESI rates
    private static final BigDecimal PF_RATE  =
            new BigDecimal("0.12");
    private static final BigDecimal ESI_RATE =
            new BigDecimal("0.0075");

    // ── Generate wage slip for one worker ────────

    @Transactional
    public WageSlipResponse generateWageSlip(
            UUID workerId,
            int year,
            int month) throws Exception {

        Worker worker = workerRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new RuntimeException("Worker not found"));

        Site site = worker.getSite();
        LocalDate slipMonth = LocalDate.of(year, month, 1);

        // Check if already generated
        wageSlipRepository
                .findByWorkerIdAndSlipMonthAndSiteId(
                        workerId, slipMonth, site.getId())
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "Wage slip already generated for " +
                                    "this month — delete first to regenerate");
                });

        // Get all attendance for this worker this month
        List<Attendance> records =
                attendanceRepository.findByWorkerAndMonth(
                        workerId, year, month);

        // Count attendance types
        int fullDays = (int) records.stream()
                .filter(a -> a.getAttendanceType()
                        == AttendanceTypeEnum.FULL_DAY)
                .count();

        int halfDays = (int) records.stream()
                .filter(a -> a.getAttendanceType()
                        == AttendanceTypeEnum.HALF_DAY)
                .count();

        int otDays = (int) records.stream()
                .filter(a -> a.getAttendanceType()
                        == AttendanceTypeEnum.OVERTIME
                        || a.getAttendanceType()
                        == AttendanceTypeEnum.DOUBLE_OVERTIME)
                .count();

        int totalPresent = (int) records.stream()
                .filter(a -> a.getAttendanceType()
                        != AttendanceTypeEnum.ABSENT)
                .count();

        // Sum gross wage from pre-computed values
        BigDecimal grossWage = records.stream()
                .map(Attendance::getComputedWage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate deductions
        BigDecimal pfDeduction = grossWage
                .multiply(PF_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal esiDeduction = grossWage
                .multiply(ESI_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal netWage = grossWage
                .subtract(pfDeduction)
                .subtract(esiDeduction);

        // Check minimum wage compliance
        checkMinWageCompliance(worker, grossWage, totalPresent);

        // Save wage slip record
        WageSlip slip = WageSlip.builder()
                .worker(worker)
                .site(site)
                .contractor(worker.getContractor())
                .slipMonth(slipMonth)
                .totalDaysPresent(totalPresent)
                .fullDays(fullDays)
                .halfDays(halfDays)
                .overtimeDays(otDays)
                .grossWage(grossWage)
                .pfDeduction(pfDeduction)
                .esiDeduction(esiDeduction)
                .netWage(netWage)
                .build();

        slip = wageSlipRepository.save(slip);

        // Build response
        WageSlipResponse response = mapToResponse(
                slip, worker);

        // Generate PDF
        byte[] pdfBytes = pdfUtil.generate(response);
        String pdfPath  = savePdf(
                pdfBytes, worker.getWorkerCode(), year, month);
        slip.setPdfUrl(pdfPath);
        slip.setGeneratedAt(Instant.now());
        wageSlipRepository.save(slip);
        response.setPdfUrl(pdfPath);

        return response;
    }

    // ── Generate for all workers on a site ───────

    @Transactional
    public List<WageSlipResponse> generateAllForSite(
            UUID siteId,
            int year,
            int month) {

        return workerRepository
                .findBySiteId(siteId)
                .stream()
                .map(worker -> {
                    try {
                        return generateWageSlip(
                                worker.getId(), year, month);
                    } catch (Exception e) {
                        // Skip already generated slips
                        return null;
                    }
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    // ── Get wage slip PDF bytes ───────────────────

    public byte[] getWageSlipPdf(UUID slipId)
            throws Exception {
        WageSlip slip = wageSlipRepository
                .findById(slipId)
                .orElseThrow(() ->
                        new RuntimeException("Wage slip not found"));

        WageSlipResponse response =
                mapToResponse(slip, slip.getWorker());
        return pdfUtil.generate(response);
    }

    // ── Get all slips for a site + month ─────────

    public List<WageSlipResponse> getSlipsForSite(
            UUID siteId, int year, int month) {
        return wageSlipRepository
                .findBySiteAndMonth(siteId, year, month)
                .stream()
                .map(s -> mapToResponse(s, s.getWorker()))
                .collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────

    private void checkMinWageCompliance(
            Worker worker,
            BigDecimal grossWage,
            int daysPresent) {

        if (daysPresent == 0) return;

        minWageRuleRepository
                .findLatestByStateAndSkill(
                        worker.getSite().getState(),
                        worker.getSkillType())
                .ifPresent(rule -> {
                    BigDecimal expectedMin = rule
                            .getMinDailyRate()
                            .multiply(
                                    BigDecimal.valueOf(daysPresent));
                    if (grossWage.compareTo(expectedMin) < 0) {
                        System.out.println(
                                "WARNING: Worker " +
                                        worker.getWorkerCode() +
                                        " paid below minimum wage!");
                    }
                });
    }

    private String savePdf(byte[] pdfBytes,
                           String workerCode,
                           int year,
                           int month) throws Exception {
        String dir = uploadDir + "wageslips/";
        Files.createDirectories(Paths.get(dir));
        String path = dir + workerCode
                + "-" + year + "-" + month + ".pdf";
        try (FileOutputStream fos =
                     new FileOutputStream(path)) {
            fos.write(pdfBytes);
        }
        return path;
    }

    private WageSlipResponse mapToResponse(
            WageSlip s, Worker w) {
        WageSlipResponse res = new WageSlipResponse();
        res.setId(s.getId());
        res.setWorkerId(w.getId());
        res.setWorkerName(w.getFullName());
        res.setWorkerCode(w.getWorkerCode());
        res.setSkillType(w.getSkillType().name());
        res.setSiteId(s.getSite().getId());
        res.setSiteName(s.getSite().getSiteName());
        res.setContractorName(
                s.getContractor().getCompanyName());
        res.setSlipMonth(s.getSlipMonth());
        res.setTotalDaysPresent(s.getTotalDaysPresent());
        res.setFullDays(s.getFullDays());
        res.setHalfDays(s.getHalfDays());
        res.setOvertimeDays(s.getOvertimeDays());
        res.setDailyWageRate(w.getDailyWageRate());
        res.setGrossWage(s.getGrossWage());
        res.setPfDeduction(s.getPfDeduction());
        res.setEsiDeduction(s.getEsiDeduction());
        res.setAdvanceDeduction(s.getAdvanceDeduction());
        res.setNetWage(s.getNetWage());
        res.setPdfUrl(s.getPdfUrl());
        return res;
    }
}