/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.service;

/**
 *
 * @author MIANTSUMI
 */
import com.j148.backend.Exceptions.LeaveRequestNotFoundException;

import com.j148.backend.leave_request.model.LeaveRequest;
import java.sql.SQLException;

public interface LeaveRequestServices {
    
    /**
     * Accepts a leave request, changing its status to APPROVED.
     * @param leaveRequest The leave request to be approved.
     * @return The updated leave request object.
     * @throws SQLException If a database error occurs.
     * @throws LeaveRequestNotFoundException If the leave request is not found or already processed.
     */
    LeaveRequest acceptLeaveRequest(LeaveRequest leaveRequest) throws SQLException, LeaveRequestNotFoundException;

    /**
     * Declines a leave request, changing its status to DENIED.
     * @param leaveRequest The leave request to be declined.
     * @return The updated leave request object.
     * @throws SQLException If a database error occurs.
     * @throws LeaveRequestNotFoundException If the leave request is not found or already processed.
     */
    LeaveRequest declineLeaveRequest(LeaveRequest leaveRequest) throws SQLException, LeaveRequestNotFoundException;

    /**
     * Fetches a leave request by its ID.
     * @param leaveRequest The leave request to retrieve.
     * @return The retrieved leave request.
     * @throws SQLException If a database error occurs.
     * @throws LeaveRequestNotFoundException If the leave request is not found.
     */
    LeaveRequest getLeaveRequestById(LeaveRequest leaveRequest) throws SQLException, LeaveRequestNotFoundException;
}