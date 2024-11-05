package com.j148.backend.attendance.model.Repo;

import com.j148.backend.attendance.model.Attendance;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepo {
    /**
     * Inserts a attendance record into the database
     *
     * @param attendance the attendance object containing details to be saved.
     * @return Optional of attendance if Insertion is successful, or return an Empty Optional if the Insertion was not successful
     */
    Optional<Attendance> createAttendanceRecord(Attendance attendance)throws SQLException;
    /**
     * Retrieves an attendance record by its ID.
     *
     * @param id the unique identifier of the attendance record.
     * @return an Optional containing the Attendance object if found, otherwise an empty Optional
     * */
    Optional<Attendance>getAttendanceByID(Long id)throws SQLException;
    /**
     * Updates an existing attendance record in the database
     *
     * @param attendance the Attendance object containing updated details.
     * @return Optional of attendance if the Update is successful, or return an empty Optional if Update was not successful.
     */
    Optional<Attendance>updateAttendance(Attendance attendance) throws SQLException;
/**
 * Retrieves a List of existing attendance records
 *
 * @return a list of attendance records*/

    List<Attendance>getAllAttendance()throws SQLException;
}
