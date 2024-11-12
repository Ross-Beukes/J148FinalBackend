package com.j148.backend.leave_request.model;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.files.model.FileEntity;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {
    private Long leaveRequestId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Decision decision;
    private Contractor contractor;
    private FileEntity file;


    public enum Decision {
        APPROVED, DENIED, PENDING
    }
}