package com.labourwage.labourwagesystem.repository;

import com.labourwage.labourwagesystem.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkerRepository
        extends JpaRepository<Worker, UUID> {

    boolean existsByAadhaarHash(String aadhaarHash);
    Optional<Worker> findByWorkerCode(String workerCode);
    List<Worker> findBySiteId(UUID siteId);
    List<Worker> findByContractorId(UUID contractorId);
}