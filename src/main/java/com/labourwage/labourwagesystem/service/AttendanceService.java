package com.labourwage.labourwagesystem.service;

import com.labourwage.labourwagesystem.dto.request.MarkAttendanceRequest;
import com.labourwage.labourwagesystem.dto.request.SyncAttendanceRequest;
import com.labourwage.labourwagesystem.dto.response.AttendanceResponse;
import com.labourwage.labourwagesystem.dto.response.SyncResultResponse;
import com.labourwage.labourwagesystem.entity.Attendance;
import com.labourwage.labourwagesystem.entity.Site;
import com.labourwage.labourwagesystem.entity.User;
import com.labourwage.labourwagesystem.entity.Worker;
import com.labourwage.labourwagesystem.enums.AttendanceTypeEnum;
import com.labourwage.labourwagesystem.repository.AttendanceRepository;
import com.labourwage.labourwagesystem.repository.SiteRepository;
import com.labourwage.labourwagesystem.repository.UserRepository;
import com.labourwage.labourwagesystem.repository.WorkerRepository;
import com.labourwage.labourwagesystem.util.QRCodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final WorkerRepository     workerRepository;
    private final SiteRepository       siteRepository;
    private final UserRepository       userRepository;
    private final QRCodeUtil           qrCodeUtil;

    // ── Mark single attendance (online) ──────────

    @Transactional
    public AttendanceResponse markAttendance(
            MarkAttendanceRequest req,
            String supervisorPhone) {

        // 1. Verify QR signature
        // Verify QR signature
        String[] parts = splitPayloadAndSignature(req.getQrPayload());
        if (parts == null || !qrCodeUtil.verifyQR(parts[0], parts[1]))
            throw new RuntimeException(
                    "Invalid QR code — signature mismatch");

        // 2. Extract worker ID from payload
        UUID workerId = extractWorkerIdFromPayload(parts[0]);

        // 3. Check if already marked today
        if (attendanceRepository
                .existsByWorkerIdAndAttendanceDateAndSiteId(
                        workerId,
                        LocalDate.now(),
                        req.getSiteId()))
            throw new RuntimeException(
                    "Attendance already marked for today");

        // 4. Validate timestamp — not older than 48 hours
        long hoursAgo = java.time.Duration.between(
                req.getScannedAt(),
                java.time.Instant.now()).toHours();
        if (hoursAgo > 48)
            throw new RuntimeException(
                    "Scan timestamp is too old — rejected");

        // 5. Fetch entities
        Worker worker = workerRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new RuntimeException("Worker not found"));

        Site site = siteRepository
                .findById(req.getSiteId())
                .orElseThrow(() ->
                        new RuntimeException("Site not found"));

        User supervisor = userRepository
                .findByPhone(supervisorPhone)
                .orElseThrow(() ->
                        new RuntimeException("Supervisor not found"));

        // 6. Calculate wage
        BigDecimal multiplier = getMultiplier(
                req.getAttendanceType());
        BigDecimal computedWage = worker
                .getDailyWageRate()
                .multiply(multiplier);

        // 7. Save attendance
        Attendance attendance = Attendance.builder()
                .worker(worker)
                .site(site)
                .contractor(worker.getContractor())
                .supervisor(supervisor)
                .attendanceDate(LocalDate.now())
                .attendanceType(req.getAttendanceType())
                .wageMultiplier(multiplier)
                .dailyWageRate(worker.getDailyWageRate())
                .computedWage(computedWage)
                .scannedAt(req.getScannedAt())
                .syncedAt(java.time.Instant.now())
                .build();

        attendance = attendanceRepository.save(attendance);
        return mapToResponse(attendance);
    }

    // ── Batch sync (offline records) ─────────────

    @Transactional
    public SyncResultResponse syncAttendance(
            SyncAttendanceRequest req,
            String supervisorPhone) {

        int synced    = 0;
        int conflicts = 0;
        int rejected  = 0;
        List<String> messages = new ArrayList<>();

        for (MarkAttendanceRequest record : req.getRecords()) {
            try {
                // Verify signature
                String[] parts = splitPayloadAndSignature(
                        record.getQrPayload());
                if (parts == null || !qrCodeUtil.verifyQR(
                        parts[0], parts[1])) {
                    rejected++;
                    messages.add("Rejected — invalid QR signature");
                    continue;
                }

                // Check timestamp
                long hoursAgo = java.time.Duration.between(
                        record.getScannedAt(),
                        java.time.Instant.now()).toHours();
                if (hoursAgo > 48) {
                    rejected++;
                    messages.add("Rejected — timestamp too old");
                    continue;
                }

                UUID workerId = extractWorkerIdFromPayload(
                        parts[0]);

                // Check conflict
                boolean exists = attendanceRepository
                        .existsByWorkerIdAndAttendanceDateAndSiteId(
                                workerId,
                                record.getScannedAt()
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate(),
                                record.getSiteId());

                if (exists) {
                    // Last write wins — update if newer
                    attendanceRepository
                            .findByWorkerIdAndAttendanceDateAndSiteId(
                                    workerId,
                                    record.getScannedAt()
                                            .atZone(java.time.ZoneId.systemDefault())
                                            .toLocalDate(),
                                    record.getSiteId())
                            .ifPresent(existing -> {
                                if (record.getScannedAt()
                                        .isAfter(existing.getScannedAt())) {
                                    existing.setAttendanceType(
                                            record.getAttendanceType());
                                    existing.setWageMultiplier(
                                            getMultiplier(
                                                    record.getAttendanceType()));
                                    existing.setComputedWage(
                                            existing.getDailyWageRate()
                                                    .multiply(getMultiplier(
                                                            record.getAttendanceType())));
                                    attendanceRepository.save(existing);
                                }
                            });
                    conflicts++;
                    messages.add("Conflict resolved for worker "
                            + workerId);
                    continue;
                }

                // Fresh record — save it
                markAttendance(record, supervisorPhone);
                synced++;
                messages.add("Synced worker " + workerId);

            } catch (Exception e) {
                rejected++;
                messages.add("Error: " + e.getMessage());
            }
        }

        return new SyncResultResponse(
                req.getRecords().size(),
                synced, conflicts, rejected, messages);
    }

    // ── Get today's attendance for a site ────────

    public List<AttendanceResponse> getTodayAttendance(
            UUID siteId) {
        return attendanceRepository
                .findBySiteIdAndAttendanceDate(
                        siteId, LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────

    private BigDecimal getMultiplier(
            AttendanceTypeEnum type) {
        return switch (type) {
            case FULL_DAY        -> new BigDecimal("1.0");
            case HALF_DAY        -> new BigDecimal("0.5");
            case OVERTIME        -> new BigDecimal("1.5");
            case DOUBLE_OVERTIME -> new BigDecimal("2.0");
            case ABSENT          -> BigDecimal.ZERO;
        };
    }

    private String[] splitPayloadAndSignature(
            String signedPayload) {
        int lastDot = signedPayload.lastIndexOf(".");
        if (lastDot == -1) return null;
        return new String[]{
                signedPayload.substring(0, lastDot),
                signedPayload.substring(lastDot + 1)
        };
    }

    private UUID extractWorkerIdFromPayload(
            String payload) {
        try {
            // payload is JSON — extract workerId value
            String marker = "\"workerId\":\"";
            int start = payload.indexOf(marker)
                    + marker.length();
            int end   = payload.indexOf("\"", start);
            return UUID.fromString(
                    payload.substring(start, end));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not extract worker ID from QR");
        }
    }

    private AttendanceResponse mapToResponse(
            Attendance a) {
        AttendanceResponse res = new AttendanceResponse();
        res.setId(a.getId());
        res.setWorkerId(a.getWorker().getId());
        res.setWorkerName(a.getWorker().getFullName());
        res.setWorkerCode(a.getWorker().getWorkerCode());
        res.setSiteId(a.getSite().getId());
        res.setAttendanceDate(a.getAttendanceDate());
        res.setAttendanceType(a.getAttendanceType());
        res.setWageMultiplier(a.getWageMultiplier());
        res.setDailyWageRate(a.getDailyWageRate());
        res.setComputedWage(a.getComputedWage());
        res.setScannedAt(a.getScannedAt());
        res.setEntryType(a.getEntryType());
        res.setVerified(a.isVerified());
        res.setDisputed(a.isDisputed());
        return res;
    }
}