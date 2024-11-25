package com.j148.backend.leave_request.repo;

import com.j148.backend.leave_request.model.LeaveRequest;

import java.sql.SQLException;
import java.util.List;

public class LeaveRequestTest {
    public static void main(String[] args) {
        // Create an instance of the class where `retrieveAllLeaveRequest` is defined
        LeaveRequestRepo leaveRequestRepository = new LeaveRequestRepoImpl();

        try {
            // Call the method to retrieve all leave requests
            List<LeaveRequest> leaveRequests = leaveRequestRepository.retrieveAllLeaveRequest();

            // Print the results
            if (leaveRequests.isEmpty()) {
                System.out.println("No leave requests found.");
            } else {
                System.out.println("Retrieved Leave Requests:");
                for (LeaveRequest leaveRequest : leaveRequests) {System.out.println("-------------------------------------");
                    System.out.println("Leave Request ID: " + leaveRequest.getLeaveRequestId());
                    System.out.println("Start Date: " + leaveRequest.getStartDate());
                    System.out.println("End Date: " + leaveRequest.getEndDate());
                    System.out.println("Decision: " + leaveRequest.getDecision());
                    System.out.println("Contractor Name: " + leaveRequest.getContractor().getUser().getName());
                    System.out.println("Contractor Email: " + leaveRequest.getContractor().getUser().getEmail());
                    System.out.println("File Path: " + leaveRequest.getFile().getPath());
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.err.println("Error retrieving leave requests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
