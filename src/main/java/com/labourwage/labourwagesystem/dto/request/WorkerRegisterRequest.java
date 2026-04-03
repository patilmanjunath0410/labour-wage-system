package com.labourwage.labourwagesystem.dto.request;

import com.labourwage.labourwagesystem.enums.SkillTypeEnum;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class WorkerRegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",
            message = "Enter valid 10 digit phone number")
    private String phone;

    @NotBlank(message = "Aadhaar is required")
    @Pattern(regexp = "^\\d{12}$",
            message = "Aadhaar must be 12 digits")
    private String aadhaar;

    private String eShramCardNo;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Skill type is required")
    private SkillTypeEnum skillType;

    @NotNull(message = "Daily wage rate is required")
    @DecimalMin(value = "1.0",
            message = "Daily wage must be greater than 0")
    private BigDecimal dailyWageRate;

    private String bankAccount;
    private String ifscCode;
    private String upiId;
    private String emergencyContact;

    @NotNull(message = "Site ID is required")
    private UUID siteId;
}