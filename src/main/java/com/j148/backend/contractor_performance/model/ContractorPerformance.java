package com.j148.backend.contractor_performance.model;

import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.user.model.User;
import com.j148.backend.warning.model.Warning;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractorPerformance {

    @Builder.Default
    private List<Warning> warningList = new ArrayList<>();  // Multiple warnings for a contractor
    
    @Builder.Default
    private List<Attendance> attendanceList = new ArrayList<>();  // Multiple attendance records for a contractor
    
    @Builder.Default
    private List<Hearing> hearingList = new ArrayList<>();  // Multiple hearings for a contractor
    
    private Contractor contractor;  // Singular contractor object (one contractor per performance)
    
    private ContractPeriod contractPeriod;  // Singular contract period (one contract period per performance)
    
    private User user;  // Singular user object (one user per performance)
    
    private AptitudeTest aptitudeTest;  // Singular aptitude test for a contractor
    
}
