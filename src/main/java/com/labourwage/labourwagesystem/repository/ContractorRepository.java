package com.labourwage.labourwagesystem.repository;

import com.labourwage.labourwagesystem.entity.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ContractorRepository
        extends JpaRepository<Contractor, UUID> {
    boolean existsByPhone(String phone);
}