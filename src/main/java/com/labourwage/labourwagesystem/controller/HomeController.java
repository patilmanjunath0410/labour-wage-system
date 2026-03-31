package com.labourwage.labourwagesystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Labour Wage System API");
        response.put("version", "1.0.0");
        response.put("status", "Running");
        response.put("auth_endpoint", "/api/v1/auth/register - Register new user");
        response.put("login_endpoint", "/api/v1/auth/login - Login user");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "labour-wage-system");
        return ResponseEntity.ok(response);
    }
}

