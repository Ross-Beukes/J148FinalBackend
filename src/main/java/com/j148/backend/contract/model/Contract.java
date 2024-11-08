package com.j148.backend.contract.model;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.user.model.User;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {
    private Long contractId;
    private ContractPeriod contractPeriod;
    private User user;
    private LocalDate offerDate;
    private LocalDate decisionDate;
    private LocalDate expirationDate;
    private Decision decision;
    private boolean isDeleted;

    public enum Decision {
        PENDING, REJECTED, ACCEPTED
    }
}
