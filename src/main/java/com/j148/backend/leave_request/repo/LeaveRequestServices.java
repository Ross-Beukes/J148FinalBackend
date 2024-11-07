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

public interface LeaveRequestServices {
    Optional<LeaveRequest> acceptLeaveRequest(LeaveRequest leaveRequest) throws SQLException;
    Optional<LeaveRequest> declineLeaveRequest(LeaveRequest leaveRequest) throws SQLException;
    Optional<LeaveRequest> getLeaveRequestById(LeaveRequest leaveRequest) throws SQLException;
}