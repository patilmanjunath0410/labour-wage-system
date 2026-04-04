package com.labourwage.labourwagesystem.controller;

import com.labourwage.labourwagesystem.dto.request.WorkerRegisterRequest;
import com.labourwage.labourwagesystem.dto.response.WorkerResponse;
import com.labourwage.labourwagesystem.entity.User;
import com.labourwage.labourwagesystem.entity.Worker;
import com.labourwage.labourwagesystem.repository.UserRepository;
import com.labourwage.labourwagesystem.repository.WorkerRepository;
import com.labourwage.labourwagesystem.service.WorkerService;
import com.labourwage.labourwagesystem.util.QRCodeUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workers")
@RequiredArgsConstructor
public class WorkerController {


    private final WorkerService workerService;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final QRCodeUtil qrCodeUtil;

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

    @GetMapping("/{id}/qr-payload")
    public ResponseEntity<String> getQRPayload(
            @PathVariable UUID id) {

        Worker worker = workerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Worker not found"));

        String signedPayload = qrCodeUtil.buildSignedPayload(
                worker.getId().toString(),
                worker.getWorkerCode(),
                worker.getSite().getId().toString(),
                "2026-04-03T06:00:00Z"
        );

        return ResponseEntity.ok(signedPayload);
    }
}