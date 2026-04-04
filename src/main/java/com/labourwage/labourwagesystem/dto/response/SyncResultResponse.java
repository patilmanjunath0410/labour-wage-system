package com.labourwage.labourwagesystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class SyncResultResponse {
    private int totalReceived;
    private int synced;
    private int conflicts;
    private int rejected;
    private List<String> messages;
}