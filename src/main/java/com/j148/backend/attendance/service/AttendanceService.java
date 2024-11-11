package com.j148.backend.attendance.service;

import com.j148.backend.attendance.model.Attendance;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    Optional<Attendance>createAttendanceRecord(Attendance attendance)throws SQLException;

    Optional<Attendance> getAttendanceById(Long id)throws SQLException;

    Optional<Attendance> updateAttendance(Attendance attendance)throws SQLException;

    List<Attendance> getAllAttendanceRecords()throws SQLException;

    Optional<Attendance> FindAllAttendanceForContractor(Long contractorId)throws SQLException;
}
