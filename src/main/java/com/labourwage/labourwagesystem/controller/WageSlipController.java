package com.labourwage.labourwagesystem.controller;

import com.labourwage.labourwagesystem.dto.response.WageSlipResponse;
import com.labourwage.labourwagesystem.service.WageCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wage-slips")
@RequiredArgsConstructor
public class WageSlipController {

    private final WageCalculationService wageCalculationService;

    // Generate wage slip for one worker
    @PostMapping("/generate/{workerId}/{year}/{month}")
    public ResponseEntity<WageSlipResponse> generate(
            @PathVariable UUID workerId,
            @PathVariable int year,
            @PathVariable int month) throws Exception {
        return ResponseEntity.ok(
                wageCalculationService.generateWageSlip(
                        workerId, year, month));
    }

    // Generate for all workers on a site
    @PostMapping("/generate-all/{siteId}/{year}/{month}")
    public ResponseEntity<List<WageSlipResponse>> generateAll(
            @PathVariable UUID siteId,
            @PathVariable int year,
            @PathVariable int month) {
        return ResponseEntity.ok(
                wageCalculationService.generateAllForSite(
                        siteId, year, month));
    }

    // Download wage slip as PDF
    @GetMapping(value = "/pdf/{slipId}",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable UUID slipId) throws Exception {
        byte[] pdf = wageCalculationService
                .getWageSlipPdf(slipId);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=wageslip.pdf")
                .body(pdf);
    }

    // Get all wage slips for a site and month
    @GetMapping("/site/{siteId}/{year}/{month}")
    public ResponseEntity<List<WageSlipResponse>> getBySite(
            @PathVariable UUID siteId,
            @PathVariable int year,
            @PathVariable int month) {
        return ResponseEntity.ok(
                wageCalculationService.getSlipsForSite(
                        siteId, year, month));
    }
}