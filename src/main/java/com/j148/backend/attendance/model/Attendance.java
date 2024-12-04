package com.j148.backend.attendance.model;
import com.j148.backend.contractor.model.Contractor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {
    @NotNull(message = "Attendance ID cannot be Null")
    @Min(value = 1, message = "Attendance ID cannot be less than 1")
    private Long attendanceId;
    @NotBlank(message = "Time-in is required")
    private LocalDateTime timeIn;
    @NotBlank(message = " Time-out is required")
    private LocalDateTime timeOut;
    private Register register;
    
    private Contractor contractor;

    public enum Register {
        PRESENT, ABSENT, LATE
    }
}

