package com.labourwage.labourwagesystem.repository;

import com.labourwage.labourwagesystem.entity.Attendance;
import com.labourwage.labourwagesystem.enums.AttendanceTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository
        extends JpaRepository<Attendance, UUID> {

    // Check duplicate — same worker same date same site
    boolean existsByWorkerIdAndAttendanceDateAndSiteId(
            UUID workerId, LocalDate date, UUID siteId);

    // Get attendance for a site on a specific date
    List<Attendance> findBySiteIdAndAttendanceDate(
            UUID siteId, LocalDate date);

    // Get all attendance for a worker in a month
    @Query("SELECT a FROM Attendance a " +
            "WHERE a.worker.id = :workerId " +
            "AND YEAR(a.attendanceDate) = :year " +
            "AND MONTH(a.attendanceDate) = :month")
    List<Attendance> findByWorkerAndMonth(
            @Param("workerId") UUID workerId,
            @Param("year")     int year,
            @Param("month")    int month);

    // Get attendance for a site in a date range
    List<Attendance> findBySiteIdAndAttendanceDateBetween(
            UUID siteId, LocalDate start, LocalDate end);

    // Find specific attendance record
    Optional<Attendance> findByWorkerIdAndAttendanceDateAndSiteId(
            UUID workerId, LocalDate date, UUID siteId);
}