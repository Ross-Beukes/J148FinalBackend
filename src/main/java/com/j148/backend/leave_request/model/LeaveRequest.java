package com.j148.backend.leave_request.model;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.files.model.FileEntity;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {
    @Min(value = 1, message = "Leave request ID cannot be less than 1")
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