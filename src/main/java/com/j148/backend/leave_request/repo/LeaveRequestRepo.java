/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.repo;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.leave_request.model.LeaveRequest;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author yusuf
 */
public interface LeaveRequestRepo {

    Optional<LeaveRequest> createLeaveRequest(LeaveRequest leaveRequest) throws SQLException;

    /*
    Creates a new leave request
     */

    AbstractMap<Long, LeaveRequest> retrieveAllContractorLeaveRequests(Contractor contractor) throws SQLException;

    /*
    Retrieves all leave requests of a specific contractor regardless of whether they are pending, approved or denied.
     */

    AbstractMap<Long, LeaveRequest> retrieveAll() throws SQLException;

    /*
    This retrieve all method maps an ID to the key and the rest of the object to the value in the key value pair making the key set the reference point for
    any updates or othr logic that may need to be implemented to a leave request object instead of returning the list of objects in an array list
     */

    AbstractMap<Long, LeaveRequest> retrieveLeaveRequestsByStartAndEndDate(LocalDate startDate, LocalDate endDate) throws SQLException;

    /*
    Retrieves all leave requests between a specific set of dates
     */

    Optional<LeaveRequest> updateLeaveRequestToApprovedOrDenied(LeaveRequest leaveRequest) throws SQLException;
    /*
    Updates the leave request to approved or denied
     */

}
