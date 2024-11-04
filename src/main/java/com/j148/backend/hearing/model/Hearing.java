package com.j148.backend.hearing.model;
import com.j148.backend.contractor.model.Contractor;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hearing {
    private Long hearingsId;
    private LocalDateTime scheduleDate;
    private Outcome outcome;
    private String reason;
    private Contractor contractor;

    public enum Outcome {
        NULL, SUSPENDED, CLEARED
    }
}