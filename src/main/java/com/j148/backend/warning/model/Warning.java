package com.j148.backend.warning.model;
import com.j148.backend.contractor.model.Contractor;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warning {
    private Long warningId;
    private Long contractorId;
    private LocalDateTime dateIssue;
    private WarningReason reason;
    private WarningState state;
    private Contractor contractor;

    public enum WarningReason {
        LATE, ABSENT
    }

    public enum WarningState {
        APPEALED, ACTIVE, REMOVED, FINAL
    }
}