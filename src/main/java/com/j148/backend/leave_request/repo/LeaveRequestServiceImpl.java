/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.repo;

/**
 *
 * @author MIANTSUMI
 */
import com.j148.backend.leave_request.model.LeaveRequest;

import java.sql.SQLException;
import java.util.Optional;

public class LeaveRequestServiceImpl implements LeaveRequestServices {

    private final LeaveRequestRepo leaveRequestRepo;

    // Constructor to inject the LeaveRequestRepo dependency
    public LeaveRequestServiceImpl(LeaveRequestRepo leaveRequestRepo) {
        this.leaveRequestRepo = leaveRequestRepo;
    }

    /**
     * Accepts a leave request.Changes the leave request status to APPROVED.
     * @param leaveRequest
     * @return 
     * @throws java.sql.SQLException
     */
    @Override
    public Optional<LeaveRequest> acceptLeaveRequest(LeaveRequest leaveRequest) throws SQLException {
        // Fetch leave request by ID
        Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepo.retrieveById(leaveRequest.getLeaveRequestId());

        if (leaveRequestOpt.isPresent()) {
            LeaveRequest leaveRequest1 = leaveRequestOpt.get();

            // Check if the leave request is already approved or denied
            if (leaveRequest.getDecision() == LeaveRequest.Decision.PENDING) {
                leaveRequest.setDecision(LeaveRequest.Decision.APPROVED); // Set the decision to APPROVED
                return leaveRequestRepo.updateLeaveRequest(leaveRequest1);
            }
        }

        return Optional.empty(); // Return empty if request was not found or is already processed
    }

    /**
     * Declines a leave request.Changes the leave request status to DENIED.
     * @param leaveRequest
     * @return 
     * @throws java.sql.SQLException
     */
    @Override
    public Optional<LeaveRequest> declineLeaveRequest(LeaveRequest leaveRequest) throws SQLException {
        // Fetch leave request by ID
        Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepo.retrieveById(leaveRequest.getLeaveRequestId());

        if (leaveRequestOpt.isPresent()) {
            LeaveRequest leaveRequest1 = leaveRequestOpt.get();

            // Check if the leave request is already approved or denied
            if (leaveRequest.getDecision() == LeaveRequest.Decision.PENDING) {
                leaveRequest.setDecision(LeaveRequest.Decision.DENIED); // Set the decision to DENIED
                return leaveRequestRepo.updateLeaveRequest(leaveRequest1);
            }
        }

        return Optional.empty(); // Return empty if request was not found or is already processed
    }

    /**
     * Fetches a leave request by its ID.
     * @param leaveRequest
     * @return 
     * @throws java.sql.SQLException
     */
    @Override
    public Optional<LeaveRequest> getLeaveRequestById(LeaveRequest leaveRequest) throws SQLException {
        return leaveRequestRepo.retrieveById(leaveRequest.getLeaveRequestId());
    }
}