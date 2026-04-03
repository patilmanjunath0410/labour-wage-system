package com.labourwage.labourwagesystem.repository;

import com.labourwage.labourwagesystem.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SiteRepository
        extends JpaRepository<Site, UUID> {

    List<Site> findByContractorId(UUID contractorId);
    boolean existsBySiteCode(String siteCode);
}