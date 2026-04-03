package com.labourwage.labourwagesystem.service;

import com.labourwage.labourwagesystem.dto.request.WorkerRegisterRequest;
import com.labourwage.labourwagesystem.dto.response.WorkerResponse;
import com.labourwage.labourwagesystem.entity.Contractor;
import com.labourwage.labourwagesystem.entity.Site;
import com.labourwage.labourwagesystem.entity.Worker;
import com.labourwage.labourwagesystem.repository.ContractorRepository;
import com.labourwage.labourwagesystem.repository.SiteRepository;
import com.labourwage.labourwagesystem.repository.WorkerRepository;
import com.labourwage.labourwagesystem.util.QRCodeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository     workerRepository;
    private final SiteRepository       siteRepository;
    private final ContractorRepository contractorRepository;
    private final QRCodeUtil           qrCodeUtil;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Transactional
    public WorkerResponse registerWorker(
            WorkerRegisterRequest req,
            UUID contractorId) throws Exception {

        // 1. Age validation — must be 18+
        int age = Period.between(
                req.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < 18)
            throw new RuntimeException(
                    "Worker must be at least 18 years old");

        // 2. Aadhaar duplicate check
        String aadhaarHash = DigestUtils.sha256Hex(req.getAadhaar());
        if (workerRepository.existsByAadhaarHash(aadhaarHash))
            throw new RuntimeException(
                    "Worker already registered with this Aadhaar");

        // 3. Get site and contractor
        Site site = siteRepository.findById(req.getSiteId())
                .orElseThrow(() ->
                        new RuntimeException("Site not found"));

        Contractor contractor = contractorRepository
                .findById(contractorId)
                .orElseThrow(() ->
                        new RuntimeException("Contractor not found"));

        // 4. Generate unique worker code
        String workerCode = generateWorkerCode(
                site.getSiteCode());

        // 5. Save worker
        Worker worker = Worker.builder()
                .workerCode(workerCode)
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .aadhaarHash(aadhaarHash)
                .aadhaarLastFour(
                        req.getAadhaar().substring(8))
                .eShramCardNo(req.getEShramCardNo())
                .dateOfBirth(req.getDateOfBirth())
                .gender(req.getGender())
                .address(req.getAddress())
                .skillType(req.getSkillType())
                .dailyWageRate(req.getDailyWageRate())
                .ifscCode(req.getIfscCode())
                .upiId(req.getUpiId())
                .emergencyContact(req.getEmergencyContact())
                .site(site)
                .contractor(contractor)
                .build();

        // Encrypt bank account if provided
        if (req.getBankAccount() != null
                && !req.getBankAccount().isBlank()) {
            worker.setBankAccountEnc(
                    req.getBankAccount()); // encrypt later
        }

        worker = workerRepository.save(worker);

        // 6. Generate QR code and save to file
        byte[] qrBytes = qrCodeUtil.generateQR(
                worker.getId().toString(),
                worker.getWorkerCode(),
                site.getId().toString()
        );

        String qrPath = saveQR(
                qrBytes, worker.getWorkerCode());
        worker.setQrUrl(qrPath);
        workerRepository.save(worker);

        return mapToResponse(worker);
    }

    public List<WorkerResponse> getWorkersBySite(UUID siteId) {
        return workerRepository.findBySiteId(siteId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public WorkerResponse getWorkerById(UUID id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Worker not found"));
        return mapToResponse(worker);
    }

    public byte[] getWorkerQR(UUID workerId) throws Exception {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() ->
                        new RuntimeException("Worker not found"));
        return qrCodeUtil.generateQR(
                worker.getId().toString(),
                worker.getWorkerCode(),
                worker.getSite().getId().toString()
        );
    }

    // ── helpers ──────────────────────────────

    private String generateWorkerCode(String siteCode) {
        long count = workerRepository.count() + 1;
        return String.format("WRK-%s-%05d", siteCode, count);
    }

    private String saveQR(byte[] qrBytes,
                          String workerCode) throws Exception {
        String dir = uploadDir + "qr/";
        Files.createDirectories(Paths.get(dir));
        String path = dir + workerCode + ".png";
        try (FileOutputStream fos =
                     new FileOutputStream(path)) {
            fos.write(qrBytes);
        }
        return path;
    }

    private WorkerResponse mapToResponse(Worker w) {
        WorkerResponse res = new WorkerResponse();
        res.setId(w.getId());
        res.setWorkerCode(w.getWorkerCode());
        res.setFullName(w.getFullName());
        res.setPhone(w.getPhone());
        res.setAadhaarLastFour(w.getAadhaarLastFour());
        res.setDateOfBirth(w.getDateOfBirth());
        res.setGender(w.getGender());
        res.setAddress(w.getAddress());
        res.setPhotoUrl(w.getPhotoUrl());
        res.setSkillType(w.getSkillType());
        res.setDailyWageRate(w.getDailyWageRate());
        res.setUpiId(w.getUpiId());
        res.setIfscCode(w.getIfscCode());
        res.setEmergencyContact(w.getEmergencyContact());
        res.setStatus(w.getStatus());
        res.setQrUrl(w.getQrUrl());
        res.setSiteId(w.getSite().getId());
        res.setContractorId(w.getContractor().getId());
        return res;
    }
}