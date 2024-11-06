/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.attendance.service;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.attendance.repo.AttendanceRepo;
import com.j148.backend.attendance.repo.AttendanceRepoImpl;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * @author glenl
 */
public class AttendanceServiceImpl implements AttendanceService {

    private AttendanceRepo attendanceRepo = new AttendanceRepoImpl();

    @Override
    public Attendance createAttendenceRecord(Attendance attendance) throws SQLException, Exception { //check in
        if (attendance != null) {
            Attendance foundAttendance = attendanceRepo.retreiveAttendanceByContractor(attendance).
                    orElseThrow(() -> new Exception("Unable to find attendance in the database"));
            if (foundAttendance != null) {
                LocalTime targeTime = LocalTime.of(8, 30);
                LocalTime currentTime = LocalTime.now();
                if (currentTime.isAfter(targeTime)) {
                    attendance.setRegister(Attendance.Register.LATE);
                } else {
                    attendance.setRegister(Attendance.Register.PRESENT);
                }
                return this.attendanceRepo.createAttendanceRecord(attendance).
                        orElseThrow(() -> new Exception("Unable to insert attendance into the database"));
            } else {
                throw new IllegalArgumentException("Contractor has already checked in");
            }
        } else {
            throw new IllegalArgumentException("Attendance cannot be null");
        }
    }

    @Override
    public Attendance checkOut(Attendance attendance) throws SQLException, Exception { //check out
        Attendance foundAttendance = attendanceRepo.getAttendanceByID(attendance.getAttendanceId()).orElseThrow(() -> new Exception("No attendance record found"));
        if (foundAttendance != null) {
            attendance.setTimeOut(LocalDateTime.now());
            return this.attendanceRepo.updateAttendance(attendance).
                    orElseThrow(() -> new Exception("Unable to update database"));
        }
        return attendance;
    }

    @Override
    public List<Attendance> CreateAbsentContractors(List<Attendance> attendances) throws SQLException, Exception {
        if (!attendances.isEmpty()) {
            for (int i = 0; i < attendances.size(); i++) {
                attendances.get(i).setRegister(Attendance.Register.ABSENT);
                Attendance attendance = this.attendanceRepo.createAttendanceRecord(attendances.get(i)).
                        orElseThrow(() -> new Exception("unable to add attendance record"));
            }
        }
        return attendances;
    }

}
