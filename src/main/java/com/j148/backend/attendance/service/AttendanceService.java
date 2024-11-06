/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.attendance.service;

import com.j148.backend.attendance.model.Attendance;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author glenl
 */
public interface AttendanceService {

    Attendance createAttendenceRecord(Attendance attendance) throws SQLException, Exception;

    Attendance checkOut(Attendance attendance) throws SQLException, Exception;

    List<Attendance> CreateAbsentContractors(List<Attendance> attendances) throws SQLException, Exception;
}
