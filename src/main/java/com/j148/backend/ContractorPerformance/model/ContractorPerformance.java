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

    private List<Warning> warnings;
    private List<Attendance> attendanceList;
    private List<Hearing> hearings;

    private Contractor contractor;
    private ContractPeriod contractPeriod;
   private User user;

}


