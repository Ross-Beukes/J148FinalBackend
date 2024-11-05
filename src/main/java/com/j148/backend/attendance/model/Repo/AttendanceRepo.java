package com.j148.backend.attendance.model.Repo;

import com.j148.backend.attendance.model.Attendance;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepo {
    /**Inserts a attendance record into the database
     *
     * @param attendance the attendance object containing details to be saved.
     * @return true if the record was successfully inserted, false if otherwise*/
    boolean createAttendanceRecord(Attendance attendance)throws SQLException;
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
     * @param  attendance the Attendance object containing updated details.
     * @return true if the record was successfully updated, false otherwise.*/
    boolean updateAttendance(Attendance attendance) throws SQLException;


    List<Attendance>getAllAttendance()throws SQLException;
}
