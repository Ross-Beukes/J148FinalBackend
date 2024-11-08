/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.leave_request.model.LeaveRequest;
import com.sun.jdi.AbsentInformationException;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;

/**
 *
 * @author yusuf
 */
public interface LeaveRequestService {

    LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) throws Exception;

    AbstractMap<Long, LeaveRequest> retrieveAllLeaveRequests() throws Exception;

    AbstractMap<Long, LeaveRequest> retrieveAllContractorLeaveRequests(Contractor contractor) throws Exception;
    
    AbstractMap<Long, LeaveRequest> retrieveAllPendingContractorLeaveRequests(Contractor contractor) throws Exception;

    AbstractMap<Long, LeaveRequest> retrieveAllLeaveRequestsBetweenDates(LocalDate startDate, LocalDate endDate) throws Exception;

    LeaveRequest updateLeaveRequestDecision(LeaveRequest leaveRequest) throws Exception;

}
