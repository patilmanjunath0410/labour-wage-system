package com.labourwage.labourwagesystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "wage_slips",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"worker_id","slip_month","site_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WageSlip {

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

    @Column(name = "slip_month", nullable = false)
    private LocalDate slipMonth;

    @Column(name = "total_days_present", nullable = false)
    @Builder.Default
    private Integer totalDaysPresent = 0;

    @Column(name = "full_days", nullable = false)
    @Builder.Default
    private Integer fullDays = 0;

    @Column(name = "half_days", nullable = false)
    @Builder.Default
    private Integer halfDays = 0;

    @Column(name = "overtime_days", nullable = false)
    @Builder.Default
    private Integer overtimeDays = 0;

    @Column(name = "gross_wage",
            nullable = false, precision = 10, scale = 2)
    private BigDecimal grossWage;

    @Column(name = "pf_deduction",
            precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal pfDeduction = BigDecimal.ZERO;

    @Column(name = "esi_deduction",
            precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal esiDeduction = BigDecimal.ZERO;

    @Column(name = "advance_deduction",
            precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal advanceDeduction = BigDecimal.ZERO;

    @Column(name = "net_wage",
            nullable = false, precision = 10, scale = 2)
    private BigDecimal netWage;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "generated_at")
    private Instant generatedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}