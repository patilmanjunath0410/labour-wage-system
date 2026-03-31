package com.labourwage.labourwagesystem.controller;

import com.labourwage.labourwagesystem.dto.request.LoginRequest;
import com.labourwage.labourwagesystem.dto.request.RegisterRequest;
import com.labourwage.labourwagesystem.dto.response.AuthResponse;
import com.labourwage.labourwagesystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}