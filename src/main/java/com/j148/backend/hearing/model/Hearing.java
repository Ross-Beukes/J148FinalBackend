package com.j148.backend.hearing.model;
import com.j148.backend.contractor.model.Contractor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hearing {
     @Min(value = 1, message = "Hearing ID cannot be less than 1")
    private Long hearingsId;
    private LocalDateTime scheduleDate;
    private Outcome outcome;
    @NotBlank(message = "A reason for a Hearing is required")
    private String reason;
    private Contractor contractor;

    public enum Outcome {
        NULL, SUSPENDED, CLEARED
    }
}