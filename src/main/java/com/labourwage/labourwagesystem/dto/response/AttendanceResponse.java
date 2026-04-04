package com.labourwage.labourwagesystem.dto.response;

import com.labourwage.labourwagesystem.enums.AttendanceTypeEnum;
import com.labourwage.labourwagesystem.enums.EntryTypeEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class AttendanceResponse {
    private UUID id;
    private UUID workerId;
    private String workerName;
    private String workerCode;
    private UUID siteId;
    private LocalDate attendanceDate;
    private AttendanceTypeEnum attendanceType;
    private BigDecimal wageMultiplier;
    private BigDecimal dailyWageRate;
    private BigDecimal computedWage;
    private Instant scannedAt;
    private EntryTypeEnum entryType;
    private boolean isVerified;
    private boolean isDisputed;
}