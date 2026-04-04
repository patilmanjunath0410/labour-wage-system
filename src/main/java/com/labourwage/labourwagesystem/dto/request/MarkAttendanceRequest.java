package com.labourwage.labourwagesystem.dto.request;

import com.labourwage.labourwagesystem.enums.AttendanceTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class MarkAttendanceRequest {

    @NotBlank(message = "QR payload is required")
    private String qrPayload;

    @NotNull(message = "Attendance type is required")
    private AttendanceTypeEnum attendanceType;

    @NotNull(message = "Scanned at time is required")
    private Instant scannedAt;

    @NotNull(message = "Site ID is required")
    private UUID siteId;
}