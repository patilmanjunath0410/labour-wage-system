package com.labourwage.labourwagesystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;
}