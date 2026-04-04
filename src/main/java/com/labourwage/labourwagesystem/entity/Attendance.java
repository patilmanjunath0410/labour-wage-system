package com.labourwage.labourwagesystem.entity;

import com.labourwage.labourwagesystem.enums.AttendanceTypeEnum;
import com.labourwage.labourwagesystem.enums.EntryTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "attendance",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"worker_id","attendance_date","site_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id", nullable = false)
    private Contractor contractor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private User supervisor;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type", nullable = false, length = 20)
    private AttendanceTypeEnum attendanceType;

    @Column(name = "wage_multiplier",
            nullable = false, precision = 3, scale = 2)
    private BigDecimal wageMultiplier;

    @Column(name = "daily_wage_rate",
            nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyWageRate;

    @Column(name = "computed_wage",
            nullable = false, precision = 10, scale = 2)
    private BigDecimal computedWage;

    @Column(name = "scanned_at", nullable = false)
    private Instant scannedAt;

    @Column(name = "synced_at")
    private Instant syncedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 20)
    @Builder.Default
    private EntryTypeEnum entryType = EntryTypeEnum.QR_SCAN;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean isVerified = true;

    @Column(name = "override_reason")
    private String overrideReason;

    @Column(name = "is_disputed", nullable = false)
    @Builder.Default
    private boolean isDisputed = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}