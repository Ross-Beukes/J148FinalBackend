/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.attendance.service;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.attendance.repo.AttendanceRepo;
import com.j148.backend.attendance.repo.AttendanceRepoImpl;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.service.ContractorService;
import com.j148.backend.contractor.service.ContractorServiceImpl;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.service.WarningService;
import com.j148.backend.warning.service.WarningServiceImpl;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author glenl
 */
public class AttendanceServiceImpl implements AttendanceService {

    private AttendanceRepo attendanceRepo = new AttendanceRepoImpl();
    private ContractorService contractorService = new ContractorServiceImpl();
    private WarningService warningService = new WarningServiceImpl();

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
                    Contractor contractor = attendance.getContractor();
                    Warning warning = warningService.lateComingWarning(contractor);
                    //issue warning 
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
    public List<Attendance> createAbsentContractors() throws SQLException, Exception {
        List<Contractor> contractors = contractorService.findCurrentContractors();
        List<Attendance> attendances = contractorsNotCheckedIn(contractors);
        if (!attendances.isEmpty()) {
            for (int i = 0; i < attendances.size(); i++) {
                attendances.get(i).setRegister(Attendance.Register.ABSENT);
                Attendance attendance = this.attendanceRepo.createAttendanceRecord(attendances.get(i)).
                        orElseThrow(() -> new Exception("unable to add attendance record"));
                Contractor contractor = attendance.getContractor();
                Warning warning = warningService.absentWarning(contractor);
            }
        }
        return attendances;
    }

    @Override
    public List<Attendance> contractorsNotCheckedIn(List<Contractor> contractors) throws SQLException, Exception {
        List<Attendance> todayAttendances = attendanceRepo.todaysAttenance();
        List<Attendance> missingAttendances = new ArrayList<>();

        for (int i = 0; i < todayAttendances.size(); i++) {
            for (int j = 0; j < contractors.size(); j++) {
                if (!(contractors.get(j).getStatus().equals(Contractor.Status.ACTIVE))) {
                    contractors.remove(j);
                    break;
                }
                if (Objects.equals(todayAttendances.get(i).getContractor().getContractorId(), contractors.get(j).getContractorId())) {
                    contractors.remove(j);
                    break;
                }
            }
        }
        for (int i = 0; i < contractors.size(); i++) {
            Attendance attendance = Attendance.builder().build();
            attendance.setTimeIn(LocalDateTime.now());
            attendance.setContractor(contractors.get(i));
            attendance.setRegister(Attendance.Register.ABSENT);
            missingAttendances.add(attendance);
        }
        return missingAttendances;
    }

}
