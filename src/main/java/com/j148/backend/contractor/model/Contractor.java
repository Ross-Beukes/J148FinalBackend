package com.j148.backend.contractor.model;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.user.model.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contractor {
    @Min(value = 1, message = "Contractor ID cannot be less than 1")
    private Long contractorId;
   
    private Status status;
    private User user;
    private ContractPeriod contractPeriod;

    public enum Status {
        EXTERNAL, SUSPENDED, ACTIVE, ON_LEAVE
    }
}
