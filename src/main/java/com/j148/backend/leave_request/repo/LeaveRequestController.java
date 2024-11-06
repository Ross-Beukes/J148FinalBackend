/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.repo;

import com.j148.backend.leave_request.model.LeaveRequest;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 * @author MIANTSUMI
 */
public class LeaveRequestController {

    private final LeaveRequestServices leaveRequestService;

    // Constructor to inject the LeaveRequestService dependency
    public LeaveRequestController(LeaveRequestServices leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    // Accept a leave request
    public void acceptLeave(Long leaveRequestId) {
        try {
            Optional<LeaveRequest> leaveRequest = leaveRequestService.acceptLeaveRequest(leaveRequestId);
            if (leaveRequest.isPresent()) {
                System.out.println("Leave request accepted: " + leaveRequest.get());
            } else {
                System.out.println("Leave request not found or could not be updated.");
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while accepting leave request: " + e.getMessage());
        }
    }

    // Decline a leave request
    public void declineLeave(Long leaveRequestId) {
        try {
            Optional<LeaveRequest> leaveRequest = leaveRequestService.declineLeaveRequest(leaveRequestId);
            if (leaveRequest.isPresent()) {
                System.out.println("Leave request declined: " + leaveRequest.get());
            } else {
                System.out.println("Leave request not found or could not be updated.");
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while declining leave request: " + e.getMessage());
        }
    }
}