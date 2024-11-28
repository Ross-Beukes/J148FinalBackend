package com.j148.backend.hearing.model;
import com.j148.backend.contractor.model.Contractor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hearing {
    @Min(value = 1, message = "Hearing ID cannot be less than 1")
    @NotNull(message = "Hearing ID cannot be Null")
    private Long hearingsId;
    private LocalDateTime scheduleDate;
    private Outcome outcome;
    @NotBlank(message = "A reason for a Hearing is required")
    @Size(min = 10 , max = 40 , message = "Reason description is too short or too long (10 Min - 40 max)")
    private String reason;
    private Contractor contractor;

    public enum Outcome {
        NULL, SUSPENDED, CLEARED
    }
}