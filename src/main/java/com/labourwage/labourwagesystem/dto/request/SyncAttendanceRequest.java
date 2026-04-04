package com.labourwage.labourwagesystem.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class SyncAttendanceRequest {
    private List<MarkAttendanceRequest> records;
}