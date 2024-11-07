package com.j148.backend.attendance.repo;

import com.j148.backend.attendance.model.Attendance;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepo {

    /**
     * Inserts a attendance record into the database
     *
     * @param attendance the attendance object containing details to be saved.
     * @return Optional of attendance if Insertion is successful, or return an
     * Empty Optional if the Insertion was not successful
     */
    Optional<Attendance> createAttendanceRecord(Attendance attendance) throws SQLException;

    /**
     * Retrieves an attendance record by its ID.
     *
     * @param id the unique identifier of the attendance record.
     * @return an Optional containing the Attendance object if found, otherwise
     * an empty Optional
     *
     */
    Optional<Attendance> getAttendanceByID(Long id) throws SQLException;

    /**
     * Updates an existing attendance record in the database
     *
     * @param attendance the Attendance object containing updated details.
     * @return Optional of attendance if the Update is successful, or return an
     * empty Optional if Update was not successful.
     */
    Optional<Attendance> updateAttendance(Attendance attendance) throws SQLException;

    /**
     * Retrieves a List of existing attendance records
     *
     * @return a list of attendance records
     */
    List<Attendance> getAllAttendance() throws SQLException;

    /**
     * Retrieves the attendance record for a contractor on the current date.
     *
     * <p>
     * This method performs a database query to find an attendance record for
     * the specified contractor ID and today's date. If an attendance record is
     * found, it constructs an {@link Attendance} object with details such as
     * attendance ID, contractor, time-in, time-out, and register status.
     *
     * @param attendance The {@link Attendance} object containing the
     * contractor's ID to search for. This object must include the contractor
     * with a valid contractor ID.
     * @return An {@link Optional} containing the {@link Attendance} object if
     * an attendance record for the contractor is found on the current date. If
     * no record is found, returns an empty Optional.
     * @throws SQLException if there is an error connecting to the database or
     * executing the SQL statement.
     */
    Optional<Attendance> retreiveAttendanceByContractor(Attendance attendance) throws SQLException;

    /**
     * Retrieves today's attendance records.
     *
     * <p>
     * This method queries the database to fetch all attendance records where
     * the check-in time (`time_in`) matches today's date. Each record is mapped
     * to an {@link Attendance} object, which includes details such as the
     * contractor, check-in and check-out times, and the attendance register
     * status.
     *
     * @return A list of {@link Attendance} records representing contractors who
     * have checked in today.
     * @throws SQLException if there is an error accessing the database or
     * executing the SQL statement.
     */
    List<Attendance> todaysAttenance() throws SQLException;
}
