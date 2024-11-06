package com.j148.backend.contractor.model;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contractor {

    private Long contractorId;
    private Status status;
    private User user;
    private ContractPeriod contractPeriod;

    public enum Status {
        EXTERNAL, SUSPENDED, ACTIVE, ON_LEAVE
    }
}
