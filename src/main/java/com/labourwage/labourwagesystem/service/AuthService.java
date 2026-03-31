package com.labourwage.labourwagesystem.service;

import com.labourwage.labourwagesystem.dto.request.LoginRequest;
import com.labourwage.labourwagesystem.dto.request.RegisterRequest;
import com.labourwage.labourwagesystem.dto.response.AuthResponse;
import com.labourwage.labourwagesystem.entity.Contractor;
import com.labourwage.labourwagesystem.entity.User;
import com.labourwage.labourwagesystem.enums.RoleEnum;
import com.labourwage.labourwagesystem.repository.ContractorRepository;
import com.labourwage.labourwagesystem.repository.UserRepository;
import com.labourwage.labourwagesystem.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository       userRepository;
    private final ContractorRepository contractorRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtUtil              jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest req) {

        if (userRepository.existsByPhone(req.getPhone()))
            throw new RuntimeException(
                    "Phone already registered: " + req.getPhone());

        // 1. Create contractor profile
        Contractor contractor = Contractor.builder()
                .companyName(req.getCompanyName())
                .ownerName(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .state(req.getState())
                .build();
        contractorRepository.save(contractor);

        // 2. Create user account
        User user = User.builder()
                .name(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(RoleEnum.CONTRACTOR)
                .contractor(contractor)
                .build();
        userRepository.save(user);

        // 3. Return JWT token
        String token = jwtUtil.generateToken(
                user.getPhone(), user.getRole().name());

        return new AuthResponse(
                token, user.getRole().name(),
                user.getName(), user.getPhone());
    }

    public AuthResponse login(LoginRequest req) {

        User user = userRepository.findByPhone(req.getPhone())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                req.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Invalid password");

        String token = jwtUtil.generateToken(
                user.getPhone(), user.getRole().name());

        return new AuthResponse(
                token, user.getRole().name(),
                user.getName(), user.getPhone());
    }
}