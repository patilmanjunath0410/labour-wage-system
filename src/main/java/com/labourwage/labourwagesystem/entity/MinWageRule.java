package com.labourwage.labourwagesystem.entity;

import com.labourwage.labourwagesystem.enums.SkillTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "min_wage_rules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MinWageRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String state;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false, length = 30)
    private SkillTypeEnum skillType;

    @Column(name = "min_daily_rate",
            nullable = false, precision = 10, scale = 2)
    private BigDecimal minDailyRate;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}