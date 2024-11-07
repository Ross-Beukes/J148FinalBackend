package com.j148.backend.contract.model;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {
    private Long contractId;
    private ContractPeriod contractPeriod;
    private User user;
    private Date offerDate;
    private Date decisionDate;
    private Date expirationDate;
    private Contractor contractor;
    private boolean isDeleted;

    public enum Decision {
        PENDING, REJECTED, ACCEPTED
    }
}
