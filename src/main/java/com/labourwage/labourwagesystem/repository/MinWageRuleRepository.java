package com.labourwage.labourwagesystem.repository;

import com.labourwage.labourwagesystem.entity.MinWageRule;
import com.labourwage.labourwagesystem.enums.SkillTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface MinWageRuleRepository
        extends JpaRepository<MinWageRule, UUID> {

    @Query("SELECT m FROM MinWageRule m " +
            "WHERE m.state = :state " +
            "AND m.skillType = :skillType " +
            "ORDER BY m.effectiveFrom DESC")
    Optional<MinWageRule> findLatestByStateAndSkill(
            @Param("state")     String state,
            @Param("skillType") SkillTypeEnum skillType);
}