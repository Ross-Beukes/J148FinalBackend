package com.j148.backend.contract_period.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "Contract Period Id cannot be Null")
    @Min(value = 1, message = "Contract ID cannot be less than 1")
    private Long contractPeriodId;
    @NotBlank(message = "A surname is required")
    @Size(min = 1, max = 20,message ="Contract period name is an invalid length")
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}

