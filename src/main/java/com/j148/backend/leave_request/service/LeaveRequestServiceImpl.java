/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.service;

import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.DateNotFoundException;
import com.j148.backend.Exceptions.FileNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.leave_request.model.LeaveRequest;
import com.j148.backend.leave_request.model.LeaveRequest.Decision;
import com.j148.backend.leave_request.repo.LeaveReqeustRepoImpl;
import com.j148.backend.leave_request.repo.LeaveRequestRepo;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.HashMap;

/**
 *
 * @author yusuf
 */
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private LeaveRequestRepo leaveRequestRepo = new LeaveReqeustRepoImpl();

    @Override
    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) throws Exception {
        if (leaveRequest != null) {
            validateLeaveRequest(leaveRequest);
            return leaveRequestRepo.createLeaveRequest(leaveRequest).orElseThrow(()
                    -> new IllegalStateException("Could not create leave request"));
        } else {
            throw new NullPointerException("Leave request entry is null");
        }

    }

    public void validateLeaveRequest(LeaveRequest leaveRequest) throws Exception {
        if (leaveRequest.getContractor() == null) {
            throw new ContractorNotFoundException("No contractor assigned to leave request");
        }
        if (leaveRequest.getStartDate() == null) {
            throw new DateNotFoundException("No date found for start date on leave request");
        }
        if (leaveRequest.getStartDate().compareTo(LocalDate.now()) < 0) {
            throw new IllegalArgumentException("Leave request start date cannot be smaller than current date");
        }
        if (leaveRequest.getEndDate().compareTo(LocalDate.now()) < 0) {
            throw new IllegalArgumentException("Leave request end date cannot be smaller than current date");
        }
        if (leaveRequest.getEndDate() == null) {
            throw new DateNotFoundException("No date found for end date on leave request");
        }
        if (leaveRequest.getFile() == null) {
            throw new FileNotFoundException("No file found for leave request");
        }
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAllLeaveRequests() throws Exception {
        HashMap<Long, LeaveRequest> copyMap = (HashMap<Long, LeaveRequest>) leaveRequestRepo.retrieveAll();
        for(Long l : copyMap.keySet()){
            if(l == 0 || l == null){
                throw new IllegalArgumentException("Invalid ID in key set (null or 0) for retrieve all leave requests map");
            }
            if(copyMap.get(l) == null){
                throw new IllegalArgumentException("Leave Request Map cannot have null values");
            }
        }
        return copyMap;
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAllContractorLeaveRequests(Contractor contractor) throws Exception {
        if (contractor != null) {
            return leaveRequestRepo.retrieveAllPendingContractorLeaveRequests(contractor);
        } else {
            throw new NullPointerException("Contractor cannot be null when retrieving all contractor leave requests");
        }
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAllLeaveRequestsBetweenDates(LocalDate startDate, LocalDate endDate) throws Exception {
        if (startDate != null || endDate != null) {
            return leaveRequestRepo.retrieveLeaveRequestsByStartAndEndDate(startDate, endDate);
        } else {
            throw new NullPointerException("Start and end dates cannot be null when retrieving leave requests in date range");
        }
    }

    @Override
    public LeaveRequest updateLeaveRequestDecision(LeaveRequest leaveRequest) throws Exception {
        if (leaveRequest != null) {
            validateUpdateLeaveRequestDecision(leaveRequest);
            return leaveRequestRepo.updateLeaveRequestToApprovedOrDenied(leaveRequest).orElseThrow(()
                    -> new Exception("There was an error updating the leave request decision"));
        } else {
            throw new NullPointerException("Leave request cannot be null in updating");
        }
    }

    public void validateUpdateLeaveRequestDecision(LeaveRequest leaveRequest) {
        if (!(leaveRequest.getDecision().equals(Decision.APPROVED) || leaveRequest.getDecision().equals(Decision.DENIED))) {
            throw new IllegalArgumentException("Invalid entry for leave request decision");
        }
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAllPendingContractorLeaveRequests(Contractor contractor) throws Exception {
        if (contractor != null) {
            return leaveRequestRepo.retrieveAllPendingContractorLeaveRequests(contractor);
        } else {
            throw new NullPointerException("Contractor cannot be null when retrieving all pending leave requests");
        }
    }

}
