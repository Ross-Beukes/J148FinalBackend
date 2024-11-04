package com.j148.backend.contract_period.model;
import lombok.*;
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

