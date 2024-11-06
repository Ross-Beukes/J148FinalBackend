package com.j148.backend.ContractorPerformance.model;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.user.model.User;
import com.j148.backend.warning.model.Warning;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractorPerformance {

    private List<Warning> warningList;   // Multiple warnings for a contractor
    private List<Attendance> attendanceList;  // Multiple attendance records for a contractor
    private List<Hearing> hearingList;  // Multiple hearings for a contractor
    private Contractor contractor;  // Singular contractor object (one contractor per performance)
    private ContractPeriod contractPeriod;  // Singular contract period (one contract period per performance)
    private User user;  // Singular user object (one user per performance)

}
