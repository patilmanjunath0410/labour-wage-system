package com.labourwage.labourwagesystem.controller;

import com.labourwage.labourwagesystem.dto.request.WorkerRegisterRequest;
import com.labourwage.labourwagesystem.dto.response.WorkerResponse;
import com.labourwage.labourwagesystem.entity.User;
import com.labourwage.labourwagesystem.repository.UserRepository;
import com.labourwage.labourwagesystem.service.WorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService  workerService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<WorkerResponse> register(
            @Valid @RequestBody WorkerRegisterRequest req,
            Principal principal) throws Exception {

        User user = userRepository
                .findByPhone(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return ResponseEntity.ok(
                workerService.registerWorker(
                        req, user.getContractor().getId()));
    }

    @GetMapping("/site/{siteId}")
    public ResponseEntity<List<WorkerResponse>> getBySite(
            @PathVariable UUID siteId) {
        return ResponseEntity.ok(
                workerService.getWorkersBySite(siteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkerResponse> getById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                workerService.getWorkerById(id));
    }

    @GetMapping(value = "/{id}/qr",
            produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQR(
            @PathVariable UUID id) throws Exception {
        return ResponseEntity.ok(
                workerService.getWorkerQR(id));
    }
}