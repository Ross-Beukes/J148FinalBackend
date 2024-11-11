package com.j148.backend.contract_period.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractPeriod {
    private Long contractPeriodId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}

