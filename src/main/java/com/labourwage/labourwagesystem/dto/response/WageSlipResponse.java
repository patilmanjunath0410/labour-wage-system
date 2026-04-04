package com.labourwage.labourwagesystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class WageSlipResponse {
    private UUID id;
    private UUID workerId;
    private String workerName;
    private String workerCode;
    private String skillType;
    private UUID siteId;
    private String siteName;
    private String contractorName;
    private LocalDate slipMonth;
    private Integer totalDaysPresent;
    private Integer fullDays;
    private Integer halfDays;
    private Integer overtimeDays;
    private BigDecimal dailyWageRate;
    private BigDecimal grossWage;
    private BigDecimal pfDeduction;
    private BigDecimal esiDeduction;
    private BigDecimal advanceDeduction;
    private BigDecimal netWage;
    private String pdfUrl;
}