package com.j148.backend.attendance.service;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.attendance.repo.AttendanceRepo;
import com.j148.backend.attendance.repo.AttendanceRepoImpl;
import com.j148.backend.config.DBConfig;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.service.ContractorService;
import com.j148.backend.contractor.service.ContractorServiceImpl;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.hearing.service.HearingService;
import com.j148.backend.hearing.service.HearingServiceImpl;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.service.WarningService;
import com.j148.backend.warning.service.WarningServiceImpl;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author glenl
 */

public class AttendanceServiceImpl implements AttendanceService {

    @Inject
    private AttendanceRepo attendanceRepo;
    @Inject
    private ContractorService contractorService;
    @Inject
    private WarningService warningService;
    @Inject
    private HearingService hearingService;

    @Transactional(dontRollbackOn = { IllegalArgumentException.class, IllegalStateException.class},rollbackOn = {SQLException.class})
    @Override
    public Attendance createAttendenceRecord(Attendance attendance) throws SQLException, Exception { //check in

        if (attendance != null && attendance.getContractor().getContractorId() != null) {
            Attendance foundAttendance;
            foundAttendance = attendanceRepo.retreiveAttendanceByContractor(attendance).orElse(null);

            if (foundAttendance == null) {
                attendance.setTimeIn(LocalDateTime.now());
                LocalTime targetTime = LocalTime.of(8, 30);
                LocalTime currentTime = LocalTime.now();
                if (currentTime.isAfter(targetTime)) {
                    attendance.setRegister(Attendance.Register.LATE);
                    Contractor contractor = attendance.getContractor();
                    Warning warning = warningService.lateComingWarning(contractor);
                    Hearing hearing = hearingService.IssueHearing(contractor);
                    //issue warning 
                } else {
                    attendance.setRegister(Attendance.Register.PRESENT);
                }
                return this.attendanceRepo.createAttendanceRecord(attendance).
                        orElseThrow(() -> new RuntimeException("Unable to insert attendance into the database"));
            } else {
                throw new IllegalArgumentException("Contractor has already checked in");
            }
        } else {
            throw new IllegalArgumentException("Attendance cannot be null");
        }
    }

    @Transactional(dontRollbackOn = { IllegalArgumentException.class, IllegalStateException.class},rollbackOn = {SQLException.class})
    @Override
    public Attendance checkOut(Attendance attendance) throws SQLException, Exception { //check out
        Attendance foundAttendance;
        if (attendance.getAttendanceId() != null) {
            foundAttendance = attendanceRepo.getAttendanceByID(attendance.getAttendanceId()).orElse(null);
        } else if (attendance.getContractor().getContractorId() != null) {
            foundAttendance = attendanceRepo.retreiveAttendanceByContractor(attendance).orElse(null);
        } else {
            foundAttendance = null;
        }
        if (foundAttendance != null) {
            Long attendanceId = foundAttendance.getAttendanceId();
            LocalDateTime timeIn = foundAttendance.getTimeIn();
            LocalDateTime timeOut = foundAttendance.getTimeOut();
            Attendance.Register register = foundAttendance.getRegister();
            Contractor contractor = foundAttendance.getContractor();
            Long contractorID = contractor.getContractorId();
            if (timeOut != null) {
                throw new RuntimeException("Contractor already checked out");
            }
            if (attendanceId != 0L && timeIn != null && register != null && contractorID != 0L) {
                foundAttendance.setTimeOut(LocalDateTime.now());
                return this.attendanceRepo.updateAttendance(foundAttendance).
                        orElseThrow(() -> new RuntimeException("Unable to update database"));
            }
        }
        return attendance;
    }

    @Transactional(dontRollbackOn = { IllegalArgumentException.class, IllegalStateException.class},rollbackOn = {SQLException.class})
    @Override
    public List<Attendance> createAbsentContractors() throws SQLException, Exception {
        List<Contractor> contractors = contractorService.findCurrentContractors();
        List<Attendance> attendances = contractorsNotCheckedIn(contractors);
        if (!attendances.isEmpty() && !contractors.isEmpty()) {
            for (Attendance value : attendances) {
                value.setRegister(Attendance.Register.ABSENT);
                Attendance attendance = this.attendanceRepo.createAttendanceRecord(value).
                        orElseThrow(() -> new RuntimeException("unable to add attendance record"));
                Contractor contractor = attendance.getContractor();
                Warning warning = warningService.absentWarning(contractor);
                Hearing hearing = hearingService.IssueHearing(contractor);
            }
        }
        return attendances;
    }

    @Override
    public List<Attendance> contractorsNotCheckedIn(List<Contractor> contractors) throws SQLException, Exception {
        List<Attendance> todayAttendances = attendanceRepo.todayAttendance();
        List<Attendance> missingAttendances = new ArrayList<>();
        for (Attendance todayAttendance : todayAttendances) {
            for (int j = 0; j < contractors.size(); j++) {
                if (!(contractors.get(j).getStatus().equals(Contractor.Status.ACTIVE))) {
                    contractors.remove(j);
                    break;
                }
                if (Objects.equals(todayAttendance.getContractor().getContractorId(), contractors.get(j).getContractorId())) {
                    contractors.remove(j);
                    break;
                }
            }
        }
        for (Contractor contractor : contractors) {
            Attendance attendance = Attendance.builder().build();
            attendance.setTimeIn(LocalDateTime.now());
            attendance.setContractor(contractor);
            attendance.setRegister(Attendance.Register.ABSENT);
            missingAttendances.add(attendance);
        }
        return missingAttendances;
    }

}
