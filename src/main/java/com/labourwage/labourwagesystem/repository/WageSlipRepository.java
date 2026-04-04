package com.labourwage.labourwagesystem.repository;

import com.labourwage.labourwagesystem.entity.WageSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WageSlipRepository
        extends JpaRepository<WageSlip, UUID> {

    Optional<WageSlip> findByWorkerIdAndSlipMonthAndSiteId(
            UUID workerId, LocalDate slipMonth, UUID siteId);

    List<WageSlip> findBySiteIdAndSlipMonth(
            UUID siteId, LocalDate slipMonth);

    List<WageSlip> findByWorkerId(UUID workerId);

    @Query("SELECT w FROM WageSlip w " +
            "WHERE w.site.id = :siteId " +
            "AND YEAR(w.slipMonth) = :year " +
            "AND MONTH(w.slipMonth) = :month")
    List<WageSlip> findBySiteAndMonth(
            @Param("siteId") UUID siteId,
            @Param("year")   int year,
            @Param("month")  int month);
}