package com.labourwage.labourwagesystem.entity;

import com.labourwage.labourwagesystem.enums.SkillTypeEnum;
import com.labourwage.labourwagesystem.enums.WorkerStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "workers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "worker_code", nullable = false, unique = true)
    private String workerCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(name = "aadhaar_hash", nullable = false, unique = true)
    private String aadhaarHash;

    @Column(name = "aadhaar_last_four", nullable = false)
    private String aadhaarLastFour;

    @Column(name = "e_shram_card_no")
    private String eShramCardNo;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String address;

    @Column(name = "photo_url")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false, length = 30)
    private SkillTypeEnum skillType;

    @Column(name = "daily_wage_rate", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal dailyWageRate;

    @Column(name = "bank_account_enc")
    private String bankAccountEnc;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "upi_id")
    private String upiId;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkerStatusEnum status = WorkerStatusEnum.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id", nullable = false)
    private Contractor contractor;

    @Column(name = "qr_url")
    private String qrUrl;

    @Column(name = "otp_verified")
    private boolean otpVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}