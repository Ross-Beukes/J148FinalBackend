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
import com.j148.backend.leave_request.repo.LeaveRequestRepo;
import com.j148.backend.leave_request.repo.LeaveRequestRepoImpl;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author yusuf
 */
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private LeaveRequestRepo leaveRequestRepo = new LeaveRequestRepoImpl();

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
        if (leaveRequest.getEndDate().isBefore(leaveRequest.getStartDate())) {
            throw new IllegalArgumentException("End date of leave request cannot be smaller than start date");
        }
        if ((leaveRequest.getEndDate().getDayOfYear() - leaveRequest.getStartDate().getDayOfYear()) > 14) {
            throw new IllegalArgumentException("Leave day requests cannot be a date range greater than 14 days");
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
        for (Long l : copyMap.keySet()) {
            if (l == 0 || l == null) {
                throw new IllegalArgumentException("Invalid ID in key set (null or 0) for retrieve all leave requests map");
            }
            if (copyMap.get(l) == null) {
                throw new IllegalArgumentException("Leave Request Map cannot have null values");
            }
        }
        return copyMap;
    }
    public List<LeaveRequest>retrieveAllLeaveRequest()throws Exception{
        HashMap<Long, LeaveRequest> copyMap = (HashMap<Long, LeaveRequest>) leaveRequestRepo.retrieveAll();
        for (Long l : copyMap.keySet()) {
            if (l == 0 || l == null) {
                throw new IllegalArgumentException("Invalid ID in key set (null or 0) for retrieve all leave requests map");
            }
            if (copyMap.get(l) == null) {
                throw new IllegalArgumentException("Leave Request Map cannot have null values");
            }
        }
        return new ArrayList<>(copyMap.values());
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAllContractorLeaveRequests(Contractor contractor) throws Exception {
        if (contractor != null) {
            AbstractMap<Long, LeaveRequest> returnedMap = leaveRequestRepo.retrieveAllContractorLeaveRequests(contractor);
            if (returnedMap != null) {
                return returnedMap;
            } else {
                throw new NullPointerException("Returned list of contractor leave requests is null");
            }

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
            LeaveRequest foundRequest = retrieveLeaveRequestByID(leaveRequest);
            if (foundRequest.getDecision() == Decision.APPROVED || foundRequest.getDecision() == Decision.DENIED) {
                throw new IllegalStateException("Leave request has already been approved or denied, cannot change value");
            }
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

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAllLeaveRequestsByDecision(String decision) throws Exception {
        if (decision != null) {
            return leaveRequestRepo.retrieveAllLeaveRequestsByDecision(decision);
        } else {
            throw new NullPointerException("Decision cannot be null when retrieving all leave requests of a specific decision");
        }
    }

    @Override
    public LeaveRequest retrieveLeaveRequestByID(LeaveRequest leaveRequest) throws Exception {
        if (leaveRequest != null) {
            validateLeaveRequestRetrievalByID(leaveRequest);
            return leaveRequestRepo.retrieveLeaveRequestByID(leaveRequest).orElseThrow(()
                    -> new Exception("There was an error in retrieving leave request by ID"));
        } else {
            throw new NullPointerException("Leave request parameter cannot be null when retrieving leave request by ID");
        }
    }

    private void validateLeaveRequestRetrievalByID(LeaveRequest leaveRequest) {
        if (leaveRequest.getLeaveRequestId() == 0) {
            throw new NullPointerException("Cannot retrieve leave request without a valid ID, ID missing");
        }
    }

}
