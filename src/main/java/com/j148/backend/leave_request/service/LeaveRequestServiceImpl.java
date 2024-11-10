/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.service;
import com.j148.backend.Exceptions.LeaveRequestNotFoundException;
import com.j148.backend.leave_request.model.LeaveRequest;
import com.j148.backend.leave_request.repo.LeaveRequestRepo;

import java.sql.SQLException;

public abstract class LeaveRequestServiceImpl implements LeaveRequestServices {

    private final LeaveRequestRepo leaveRequestRepo;

    // Constructor to inject the LeaveRequestRepo dependency
    public LeaveRequestServiceImpl(LeaveRequestRepo leaveRequestRepo) {
        this.leaveRequestRepo = leaveRequestRepo;
    }

    /**
     * Accepts a leave request, changes its status to APPROVED.
     * @param leaveRequest The leave request to be approved.
     * @return The updated leave request object.
     * @throws SQLException If a database error occurs.
     * @throws LeaveRequestNotFoundException If the leave request is not found or already processed.
     * @throws IllegalArgumentException If the leave request ID is null.
     */
    @Override
    public LeaveRequest acceptLeaveRequest(LeaveRequest leaveRequest) throws SQLException, LeaveRequestNotFoundException {
        if (leaveRequest == null || leaveRequest.getLeaveRequestId() == null) {
            throw new IllegalArgumentException("Leave request or leave request ID cannot be null");
        }

        // Fetch leave request by ID
        LeaveRequest leaveRequestOpt = leaveRequestRepo.retrieveById(leaveRequest.getLeaveRequestId())
                .orElseThrow(() -> new LeaveRequestNotFoundException("Leave request not found with ID: " + leaveRequest.getLeaveRequestId()));

        // Check if the leave request is already approved or denied
        if (leaveRequestOpt.getDecision() == LeaveRequest.Decision.PENDING) {
            leaveRequestOpt.setDecision(LeaveRequest.Decision.APPROVED); // Set the decision to APPROVED
            return leaveRequestRepo.updateLeaveRequest(leaveRequestOpt)
                    .orElseThrow(() -> new SQLException("Failed to update leave request in the database."));
        } else {
            throw new LeaveRequestNotFoundException("Leave request with ID " + leaveRequest.getLeaveRequestId() + " is already processed.");
        }
    }

    /**
     * Declines a leave request, changing its status to DENIED.
     * @param leaveRequest The leave request to be declined.
     * @return The updated leave request object.
     * @throws SQLException If a database error occurs.
     * @throws LeaveRequestNotFoundException If the leave request is not found or already processed.
     * @throws IllegalArgumentException If the leave request ID is null.
     */
    @Override
    public LeaveRequest declineLeaveRequest(LeaveRequest leaveRequest) throws SQLException, LeaveRequestNotFoundException {
        if (leaveRequest == null || leaveRequest.getLeaveRequestId() == null) {
            throw new IllegalArgumentException("Leave request or leave request ID cannot be null");
        }

        // Fetch leave request by ID
        LeaveRequest leaveRequestOpt = leaveRequestRepo.retrieveById(leaveRequest.getLeaveRequestId())
                .orElseThrow(() -> new LeaveRequestNotFoundException("Leave request not found with ID: " + leaveRequest.getLeaveRequestId()));

        // Check if the leave request is already approved or denied
        if (leaveRequestOpt.getDecision() == LeaveRequest.Decision.PENDING) {
            leaveRequestOpt.setDecision(LeaveRequest.Decision.DENIED); // Set the decision to DENIED
            return leaveRequestRepo.updateLeaveRequest(leaveRequestOpt)
                    .orElseThrow(() -> new SQLException("Failed to update leave request in the database."));
        } else {
            throw new LeaveRequestNotFoundException("Leave request with ID " + leaveRequest.getLeaveRequestId() + " is already processed.");
        }
    }

    /**
     * Fetches a leave request by its ID.
     * @param leaveRequest The leave request to retrieve.
     * @return The retrieved leave request.
     * @throws SQLException If a database error occurs.
     * @throws LeaveRequestNotFoundException If the leave request is not found.
     * @throws IllegalArgumentException If the leave request ID is null.
     */
    @Override
    public LeaveRequest getLeaveRequestById(LeaveRequest leaveRequest) throws SQLException, LeaveRequestNotFoundException {
        if (leaveRequest == null || leaveRequest.getLeaveRequestId() == null) {
            throw new IllegalArgumentException("Leave request or leave request ID cannot be null");
        }
        return leaveRequestRepo.retrieveById(leaveRequest.getLeaveRequestId())
                .orElseThrow(() -> new LeaveRequestNotFoundException("Leave request not found with ID: " + leaveRequest.getLeaveRequestId()));
    }
}