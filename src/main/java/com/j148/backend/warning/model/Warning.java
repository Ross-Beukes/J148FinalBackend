package com.j148.backend.warning.model;
import com.j148.backend.contractor.model.Contractor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warning {
    @Min(value = 1, message = "Warning cannot be less than 1")
    private Long warningId;
    private LocalDateTime dateIssue;
    private WarningReason reason;
    private WarningState state;
    private Contractor contractor;

    public enum WarningReason {
        LATE, ABSENT, MISCONDUCT
    }

    public enum WarningState {
        APPEALED, ACTIVE, REMOVED, FINAL
    }
}