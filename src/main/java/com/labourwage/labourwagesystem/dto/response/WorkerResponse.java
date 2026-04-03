package com.labourwage.labourwagesystem.dto.response;

import com.labourwage.labourwagesystem.enums.SkillTypeEnum;
import com.labourwage.labourwagesystem.enums.WorkerStatusEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class WorkerResponse {
    private UUID id;
    private String workerCode;
    private String fullName;
    private String phone;
    private String aadhaarLastFour;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String photoUrl;
    private SkillTypeEnum skillType;
    private BigDecimal dailyWageRate;
    private String upiId;
    private String ifscCode;
    private String emergencyContact;
    private WorkerStatusEnum status;
    private String qrUrl;
    private UUID siteId;
    private UUID contractorId;
}