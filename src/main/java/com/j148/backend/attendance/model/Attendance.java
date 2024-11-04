package com.j148.backend.attendance.model;
import com.j148.backend.contractor.model.Contractor;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {
    private Long attendanceId;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;
    private Register register;
    private Contractor contractor;

    public enum Register {
        PRESENT, ABSENT, LATE
    }
}

