/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.attendance.service;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.contractor.model.Contractor;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author glenl
 */
public interface AttendanceService {

    /**
     * Creates an attendance record for a contractor checking in.
     *
     * <p>
     * This method checks if an attendance record already exists for the
     * contractor on the current date. If none exists, it creates a new
     * attendance record. The method also determines the contractor's status as
     * either "LATE" or "PRESENT" based on whether the check-in time is after
     * the target time of 8:30 AM.
     *
     * @param attendance The {@link Attendance} object containing the details of
     * the contractor and their attendance information.
     * @return The created {@link Attendance} record with the set registration
     * status.
     * @throws SQLException if there is a database error during the operation.
     * @throws Exception if no existing attendance record is found or if the
     * record cannot be created.
     * @throws IllegalArgumentException if the attendance is null or if the
     * contractor has already checked in.
     */
    Attendance createAttendenceRecord(Attendance attendance) throws SQLException, Exception;

    /**
     * Checks out a contractor by updating their attendance record with the
     * current time.
     *
     * <p>
     * This method updates an existing attendance record to set the check-out
     * time to the current date and time. It retrieves the attendance record
     * based on the attendance ID and performs the update.
     *
     * @param attendance The {@link Attendance} object with the attendance ID
     * for the record to be updated.
     * @return The updated {@link Attendance} object with the check-out time
     * set.
     * @throws SQLException if there is a database error during the operation.
     * @throws Exception if no attendance record is found with the specified ID
     * or if the record cannot be updated.
     */
    Attendance checkOut(Attendance attendance) throws SQLException, Exception;

    /**
     * Marks a list of contractors as "ABSENT" by creating attendance records
     * for each.
     *
     * <p>
     * This method iterates over a list of {@link Attendance} objects
     * representing contractors who are absent. For each contractor, it creates
     * an attendance record with the registration status set to "ABSENT".
     *
     * @param attendances A list of {@link Attendance} objects for contractors
     * to be marked as absent.
     * @return A list of {@link Attendance} objects with updated status.
     * @throws SQLException if there is a database error during the operation.
     * @throws Exception if an attendance record cannot be created for any
     * contractor.
     */
    List<Attendance> createAbsentContractors() throws SQLException, Exception;

    /**
     * Identifies contractors who have not checked in today and marks them as
     * absent.
     *
     * <p>
     * This method first retrieves a list of today's attendance records and
     * compares it with the list of provided contractors. For each contractor
     * who is active but has no check-in record for today, an absence record is
     * created with the current time set as the check-in time. These contractors
     * are then added to a list of absent contractors.
     *
     * @param contractors A list of {@link Contractor} objects representing all
     * contractors who are expected to check in.
     * @return A list of {@link Attendance} records for contractors who have not
     * checked in, marked as absent.
     * @throws SQLException if an error occurs when retrieving today's
     * attendance records.
     * @throws Exception if a general error occurs during processing.
     */
    List<Attendance> contractorsNotCheckedIn(List<Contractor> contractors) throws SQLException, Exception;

}
