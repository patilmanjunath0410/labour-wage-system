package com.labourwage.labourwagesystem.controller;

import com.labourwage.labourwagesystem.dto.request.MarkAttendanceRequest;
import com.labourwage.labourwagesystem.dto.request.SyncAttendanceRequest;
import com.labourwage.labourwagesystem.dto.response.AttendanceResponse;
import com.labourwage.labourwagesystem.dto.response.SyncResultResponse;
import com.labourwage.labourwagesystem.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // Mark single attendance — online mode
    @PostMapping("/mark")
    public ResponseEntity<AttendanceResponse> mark(
            @Valid @RequestBody MarkAttendanceRequest req,
            Principal principal) {
        return ResponseEntity.ok(
                attendanceService.markAttendance(
                        req, principal.getName()));
    }

    // Batch sync — offline mode
    @PostMapping("/sync")
    public ResponseEntity<SyncResultResponse> sync(
            @RequestBody SyncAttendanceRequest req,
            Principal principal) {
        return ResponseEntity.ok(
                attendanceService.syncAttendance(
                        req, principal.getName()));
    }

    // Get today's attendance for a site
    @GetMapping("/site/{siteId}/today")
    public ResponseEntity<List<AttendanceResponse>> today(
            @PathVariable UUID siteId) {
        return ResponseEntity.ok(
                attendanceService.getTodayAttendance(siteId));
    }
}