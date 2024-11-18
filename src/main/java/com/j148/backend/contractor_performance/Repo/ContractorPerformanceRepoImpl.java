package com.j148.backend.contractor_performance.repo;

import com.j148.backend.contractor_performance.model.ContractorPerformance;
import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.config.DBConfig;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.user.model.User;
import com.j148.backend.warning.model.Warning;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jfree.chart.*;
import org.jfree.data.*;
import org.apache.commons.dbcp2.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xddf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.util.CellRangeAddress;

public class ContractorPerformanceRepoImpl extends DBConfig implements ContractorPerformanceRepo {

    @Override
    public Optional<ContractorPerformance> getContractorPerformance(User user) throws SQLException {
        ContractorPerformance cp = ContractorPerformance.builder()
                .warningList(new ArrayList<>())
                .attendanceList(new ArrayList<>())
                .hearingList(new ArrayList<>())
                .build();

        String query = "SELECT "
                + "user.user_id, user.name AS user_name, user.surname, user.email, user.age, user.gender, user.race,"
                + "contractor.contractor_id, contractor.status, contractor.contractor_period_id, "
                + "contractor_period.contractor_period_id, contractor_period.name AS period_name, "
                + "contractor_period.start_date, contractor_period.end_date, "
                + "warning.date_issue, warning.reason AS warning_reason, warning.state AS warning_state, "
                + "attendance.attendance_id, attendance.time_in, attendance.time_out, attendance.register AS attendance_register, "
                + "hearings.schedule_date AS hearing_schedule_date, hearings.outcome AS hearing_outcome, hearings.reason AS hearing_reason "
                + "FROM user "
                + "JOIN contractor ON user.user_id = contractor.user_id "
                + "JOIN contractor_period ON contractor.contractor_period_id = contractor_period.contractor_period_id "
                + "LEFT JOIN warning ON contractor.contractor_id = warning.contractor_id "
                + "LEFT JOIN attendance ON contractor.contractor_id = attendance.contractor_id "
                + "LEFT JOIN hearings ON contractor.contractor_id = hearings.contractor_id "
                + "WHERE user.user_id = ?;";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, user.getUserId());

            try (ResultSet rs = ps.executeQuery()) {
                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;

                    // Populate User information (only once)
                    if (cp.getUser() == null) {
                        User dbUser = User.builder()
                                .userId(rs.getLong("user_id"))
                                .name(rs.getString("user_name"))
                                .surname(rs.getString("surname"))
                                .email(rs.getString("email"))
                                .age(rs.getInt("age"))
                                .race(rs.getString("race"))
                                .gender(rs.getString("gender"))
                                .build();
                        cp.setUser(dbUser);  // Set the singular user
                    }

                    // Populate Contractor information (only once)
                    if (cp.getContractor() == null) {
                        Contractor contractor = Contractor.builder()
                                .contractorId(rs.getLong("contractor_id"))
                                .status(Contractor.Status.valueOf(rs.getString("status")))
                                .build();
                        cp.setContractor(contractor);  // Set the singular contractor
                    }

                    // Populate ContractPeriod information (only once)
                    if (cp.getContractPeriod() == null) {
                        ContractPeriod contractPeriod = ContractPeriod.builder()
                                .name(rs.getString("period_name"))
                                .startDate(rs.getDate("start_date").toLocalDate())
                                .endDate(rs.getDate("end_date").toLocalDate())
                                .build();
                        cp.setContractPeriod(contractPeriod);  // Set the singular contract period
                    }

                    // Populate Warning information (if present)
                    if (rs.getTimestamp("date_issue") != null) {
                        Warning warning = Warning.builder()
                                .dateIssue(rs.getTimestamp("date_issue").toLocalDateTime())
                                .reason(Warning.WarningReason.valueOf(rs.getString("warning_reason")))
                                .state(Warning.WarningState.valueOf(rs.getString("warning_state")))
                                .build();
                        cp.getWarningList().add(warning);  // Add warning to the list
                    }

                    // Populate Attendance information (if present)
                    if (rs.getTimestamp("time_in") != null) {
                        Attendance attendance = Attendance.builder()
                                .attendanceId(rs.getLong("attendance_id"))
                                .timeIn(rs.getTimestamp("time_in").toLocalDateTime())
                                .timeOut(rs.getTimestamp("time_out").toLocalDateTime())
                                .register(Attendance.Register.valueOf(rs.getString("attendance_register")))
                                .build();
                        cp.getAttendanceList().add(attendance);  // Add attendance to the list
                    }

                    // Populate Hearing information (if present)
                    if (rs.getTimestamp("hearing_schedule_date") != null) {
                        Hearing hearing = Hearing.builder()
                                .scheduleDate(rs.getTimestamp("hearing_schedule_date").toLocalDateTime())
                                .outcome(Hearing.Outcome.valueOf(rs.getString("hearing_outcome")))
                                .reason(rs.getString("hearing_reason"))
                                .build();
                        cp.getHearingList().add(hearing);  // Add hearing to the list
                    }
                }

                if (!hasResults) {
                    return Optional.empty();
                }
            }
        }

        return Optional.of(cp);
    }

    @Override
    public List<ContractorPerformance> getAllContractorPerformance() throws SQLException {
        List<ContractorPerformance> contractorPerformances = new ArrayList<>();
        Map<Long, ContractorPerformance> contractorMap = new HashMap<>();

        //Might need to play around with this join statement(Consider which is the left table)
        //Might need to null check in the function
        String query = "SELECT "

                + "user.user_id, user.name AS user_name, user.surname, user.email, user.age, user.gender, user.race,"
                + "contractor.contractor_id, contractor.user_id AS contractor_user_id, contractor.status, contractor.contractor_period_id, "
                + "contractor_period.contractor_period_id, contractor_period.name AS period_name, "
                + "contractor_period.start_date, contractor_period.end_date, "
                + "warning.date_issue, warning.reason AS warning_reason, warning.state AS warning_state, "
                + "user.user_id, user.name AS user_name, user.surname, user.email, user.age, user.gender, user.race, "
                + "contractor.contractor_id, contractor.status, "
                + "contractor_period.name AS period_name, contractor_period.start_date, contractor_period.end_date, "
                + "warning.warning_id, warning.date_issue, warning.reason AS warning_reason, warning.state AS warning_state, "
                + "attendance.attendance_id, attendance.time_in, attendance.time_out, attendance.register AS attendance_register, "
                + "hearings.hearings_id, hearings.schedule_date AS hearing_schedule_date, hearings.outcome AS hearing_outcome, hearings.reason AS hearing_reason, "
                + "aptitude_test.aptitude_test_id, aptitude_test.test_mark, aptitude_test.test_date "
                + "FROM user "
                + "JOIN contractor ON user.user_id = contractor.user_id "
                + "JOIN contractor_period ON contractor.contractor_period_id = contractor_period.contractor_period_id "
                + "LEFT JOIN warning ON contractor.contractor_id = warning.contractor_id "
                + "LEFT JOIN attendance ON contractor.contractor_id = attendance.contractor_id "
                + "LEFT JOIN hearings ON contractor.contractor_id = hearings.contractor_id "
                + "LEFT JOIN aptitude_test ON user.user_id = aptitude_test.user_id;";

        // HashMaps to track processed warning, hearing, and attendance IDs for each contractor
        Map<Long, Set<Long>> warningIdsMap = new HashMap<>();
        Map<Long, Set<Long>> hearingIdsMap = new HashMap<>();
        Map<Long, Set<Long>> attendanceIdsMap = new HashMap<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long contractorId = rs.getLong("contractor_id");

                    // Retrieve or create ContractorPerformance
                    ContractorPerformance cp = contractorMap.get(contractorId);
                    if (cp == null) {
                        cp = new ContractorPerformance();

                        // Populate User
                        User user = User.builder()
                                .userId(rs.getLong("user_id"))
                                .name(rs.getString("user_name"))
                                .surname(rs.getString("surname"))
                                .email(rs.getString("email"))
                                .age(rs.getInt("age"))
                                .race(rs.getString("race"))
                                .gender(rs.getString("gender"))
                                .build();
                        cp.setUser(user);

                        // Populate Contractor
                        Contractor contractor = Contractor.builder()
                                .contractorId(contractorId)
                                .status(Contractor.Status.valueOf(rs.getString("status")))
                                .build();
                        cp.setContractor(contractor);

                        // Populate ContractPeriod
                        ContractPeriod contractPeriod = ContractPeriod.builder()
                                .name(rs.getString("period_name"))
                                .startDate(rs.getDate("start_date").toLocalDate())
                                .endDate(rs.getDate("end_date").toLocalDate())
                                .build();
                        cp.setContractPeriod(contractPeriod);

                        // Initialize tracking maps for this contractor if not present
                        warningIdsMap.putIfAbsent(contractorId, new HashSet<>());
                        hearingIdsMap.putIfAbsent(contractorId, new HashSet<>());
                        attendanceIdsMap.putIfAbsent(contractorId, new HashSet<>());

                        // Add to map for tracking
                        contractorMap.put(contractorId, cp);
                    }

                    // Populate Warning

                    if (rs.getTimestamp("date_issue") != null) {
                        
                          // Populate Contractor
                        Contractor contractor = Contractor.builder()
                                .contractorId(contractorId)
                                .user(User.builder().userId(rs.getLong("contractor_user_id")).build())
                                .status(Contractor.Status.valueOf(rs.getString("status")))
                                .build();
                        cp.setContractor(contractor);
                        
                        Warning warning = Warning.builder()
                                .contractor(contractor)

                    long warningId = rs.getLong("warning_id");
                    if (warningId != 0 && !warningIdsMap.get(contractorId).contains(warningId)) {
                        Warning warning = Warning.builder()
                                .warningId(warningId)
                                .dateIssue(rs.getTimestamp("date_issue").toLocalDateTime())
                                .reason(Warning.WarningReason.valueOf(rs.getString("warning_reason")))
                                .state(Warning.WarningState.valueOf(rs.getString("warning_state")))
                                .build();
                        cp.getWarningList().add(warning);
                        warningIdsMap.get(contractorId).add(warningId); // Track processed warning ID
                    }

                    // Populate Attendance
                    long attendanceId = rs.getLong("attendance_id");
                    if (attendanceId != 0 && !attendanceIdsMap.get(contractorId).contains(attendanceId)) {
                        Attendance attendance = Attendance.builder()
                                .attendanceId(attendanceId)
                                .timeIn(rs.getTimestamp("time_in").toLocalDateTime())
                                .timeOut(rs.getTimestamp("time_out").toLocalDateTime())
                                .register(Attendance.Register.valueOf(rs.getString("attendance_register")))
                                .build();
                        cp.getAttendanceList().add(attendance);
                        attendanceIdsMap.get(contractorId).add(attendanceId); // Track processed attendance ID
                    }

                    // Populate Hearing
                    long hearingId = rs.getLong("hearings_id");
                    if (hearingId != 0 && !hearingIdsMap.get(contractorId).contains(hearingId)) {
                        Hearing hearing = Hearing.builder()
                                .hearingsId(hearingId)
                                .scheduleDate(rs.getTimestamp("hearing_schedule_date").toLocalDateTime())
                                .outcome(Hearing.Outcome.valueOf(rs.getString("hearing_outcome")))
                                .reason(rs.getString("hearing_reason"))
                                .build();
                        cp.getHearingList().add(hearing);
                        hearingIdsMap.get(contractorId).add(hearingId); // Track processed hearing ID
                    }

                    // Populate AptitudeTest if not already set (only one aptitude test per contractor)
                    if (rs.getLong("aptitude_test_id") != 0 && cp.getAptitudeTest() == null) {
                        AptitudeTest aptitudeTest = AptitudeTest.builder()
                                .aptitudeTestId(rs.getLong("aptitude_test_id"))
                                .testMark(rs.getInt("test_mark"))
                                .testDate(rs.getTimestamp("test_date").toLocalDateTime())
                                .build();
                        cp.setAptitudeTest(aptitudeTest);  // Set single AptitudeTest object
                    }
                }
            }
        }

        // Collect all ContractorPerformance objects
        contractorPerformances.addAll(contractorMap.values());
        return contractorPerformances;
    }

    @Override
    public List<ContractorPerformance> filterContractorPerformance(String filters, List<ContractorPerformance> cp) throws SQLException {
//        String[] filterList = filters.toLowerCase().split(",");
//        for (int i = 0; i < filterList.length; i++) {
//            int operatorPos = 0;
//            operatorLoop:
//            for (int j = 0; j < filterList[i].length(); j++) {
//                switch (filterList[i].charAt(j)) {
//                    case '=': {
//                        operatorPos = filterList[i].indexOf("=");
//                        String filterName = filterList[i].substring(0, operatorPos);
//                        switch (filterName) {
//                            case "attendance-time-in": {
//
//                                break;
//                            }
//                            case "attendance-register": {
//
//                                break;
//                            }
//                            case "contractor-period-start-date": {
//
//                                break;
//                            }
//                            case "contractor-period-end-date": {
//
//                                break;
//                            }
//                            case "contractor-status": {
//
//                                break;
//                            }
//                            case "hearings-schedule-date": {
//
//                                break;
//                            }
//                            case "hearings-outcome": {
//
//                                break;
//                            }
//                            case "user-gender": {
//
//                                break;
//                            }
//                            case "user-race": {
//
//                                break;
//                            }
//                            case "user-age": {
//
//                                break;
//                            }
//                            case "warning-date-issued": {
//
//                                break;
//                            }
//                            case "warning-reason": {
//
//                                break;
//                            }
//                            case "warning-state": {
//
//                                break;
//                            }
//                            case "aptitude-test-mark": {
//
//                                break;
//                            }
//                            case "aptitude-test-date": {
//
//                                break;
//                            }
//                            default:{
//                                return new ArrayList<>();
//                            }
//                        }
//                        break operatorLoop;
//                    }
//                    case '>': {
//                        operatorPos = filterList[i].indexOf(">");
//                        String filterName = filterList[i].substring(0, operatorPos);
//                        switch (filterName) {
//                            case "attendance-time-in": {
//                                
//                                break;
//                            }
//                            case "contractor-period-start-date": {
//
//                                break;
//                            }
//                            case "contractor-period-end-date": {
//
//                                break;
//                            }
//                            case "hearings-schedule-date": {
//
//                                break;
//                            }
//                            case "user-age": {
//
//                                break;
//                            }
//                            case "warning-date-issued": {
//
//                                break;
//                            }
//                            case "aptitude-test-mark": {
//
//                                break;
//                            }
//                            case "aptitude-test-date": {
//
//                                break;
//                            }
//                            default:{
//                                return new ArrayList<>();
//                            }
//                        }
//                        break operatorLoop;
//                    }
//                    case '<': {
//                        operatorPos = filterList[i].indexOf("<");
//                        String filterName = filterList[i].substring(0, operatorPos);
//                        String filterVal = filterList[i].substring(operatorPos, filterList[i].length());
//                        switch (filterName) {
//                            case "attendance-time-in": {
//
//                                break;
//                            }
//                            case "contractor-period-start-date": {
//
//                                break;
//                            }
//                            case "contractor-period-end-date": {
//
//                                break;
//                            }
//                            case "hearings-schedule-date": {
//
//                                break;
//                            }
//                            case "user-age": {
//
//                                break;
//                            }
//                            case "warning-date-issued": {
//
//                                break;
//                            }
//                            case "aptitude-test-mark": {
//
//                                break;
//                            }
//                            case "aptitude-test-date": {
//
//                                break;
//                            }
//                            default:{
//                                return new ArrayList<>();
//                            }
//                        }
//                        break operatorLoop;
//                    }
//                }
//            }
//
//        }
        String[] filterList = filters.toLowerCase().split(",");

        for (String filter : filterList) {
            int operatorPos = 0;
            String filterName;
            String filterValue;

            if (filter.contains("=")) {
                operatorPos = filter.indexOf("=");
                filterName = filter.substring(0, operatorPos);
                filterValue = filter.substring(operatorPos + 1);
            } else if (filter.contains(">")) {
                operatorPos = filter.indexOf(">");
                filterName = filter.substring(0, operatorPos);
                filterValue = filter.substring(operatorPos + 1);
            } else if (filter.contains("<")) {
                operatorPos = filter.indexOf("<");
                filterName = filter.substring(0, operatorPos);
                filterValue = filter.substring(operatorPos + 1);
            } else {
                return new ArrayList<>(); // Return empty list for invalid operator
            }

            switch (filterName) {
                case "attendance-time-in":
                    filterByAttendanceTime(cp, LocalDateTime.parse(filterValue), filter.charAt(operatorPos));
                    break;
                case "attendance-register":
                    filterByAttendanceRegister(cp, filterValue);
                    break;
                case "contractor-period-start-date":
                    filterByContractPeriodDate(cp, LocalDate.parse(filterValue), filter.charAt(operatorPos), true);
                    break;
                case "contractor-period-end-date":
                    filterByContractPeriodDate(cp, LocalDate.parse(filterValue), filter.charAt(operatorPos), false);
                    break;
                case "contractor-status":
                    filterByContractorStatus(cp, filterValue);
                    break;
                case "hearings-schedule-date":
                    filterByHearingScheduleDate(cp, LocalDateTime.parse(filterValue), filter.charAt(operatorPos));
                    break;
                case "hearings-outcome":
                    filterByHearingOutcome(cp, filterValue);
                    break;
                case "user-gender":
                    filterByUserGender(cp, filterValue);
                    break;
                case "user-race":
                    filterByUserRace(cp, filterValue);
                    break;
                case "user-age":
                    filterByUserAge(cp, Integer.parseInt(filterValue), filter.charAt(operatorPos));
                    break;
                case "warning-date-issued":
                    filterByWarningDateIssued(cp, LocalDateTime.parse(filterValue), filter.charAt(operatorPos));
                    break;
                case "warning-reason":
                    filterByWarningReason(cp, filterValue);
                    break;
                case "warning-state":
                    filterByWarningState(cp, filterValue);
                    break;
                case "aptitude-test-mark":
                    filterByAptitudeTestMark(cp, Integer.parseInt(filterValue), filter.charAt(operatorPos));
                    break;
                case "aptitude-test-date":
                    filterByAptitudeTestDate(cp, LocalDateTime.parse(filterValue), filter.charAt(operatorPos));
                    break;
                default:
                    return new ArrayList<>(); // Invalid filter name
            }
        }
        return cp;
    }

    /**
     * The following methods are helper methods used for specific filtering
     * based on filter names, operators and values
     */
    private void filterByAttendanceTime(List<ContractorPerformance> cp, LocalDateTime timeIn, char operator) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            boolean hasMatchingAttendance = false;
            for (Attendance att : cp.get(i).getAttendanceList()) {
                if (compare(att.getTimeIn(), timeIn, operator)) {
                    hasMatchingAttendance = true;
                    break;
                }
            }
            if (!hasMatchingAttendance) {
                cp.remove(i);
            }
        }
    }

    private void filterByAttendanceRegister(List<ContractorPerformance> cp, String register) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            boolean hasMatchingRegister = false;
            for (Attendance att : cp.get(i).getAttendanceList()) {
                if (att.getRegister().name().equalsIgnoreCase(register)) {
                    hasMatchingRegister = true;
                    break;
                }
            }
            if (!hasMatchingRegister) {
                cp.remove(i);
            }
        }
    }

    private void filterByContractPeriodDate(List<ContractorPerformance> cp, LocalDate date, char operator, boolean isStartDate) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            LocalDate periodDate = isStartDate ? cp.get(i).getContractPeriod().getStartDate() : cp.get(i).getContractPeriod().getEndDate();
            if (!compare(periodDate, date, operator)) {
                cp.remove(i);
            }
        }
    }

    private void filterByContractorStatus(List<ContractorPerformance> cp, String status) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            if (!cp.get(i).getContractor().getStatus().name().equalsIgnoreCase(status)) {
                cp.remove(i);
            }
        }
    }

    private void filterByHearingScheduleDate(List<ContractorPerformance> cp, LocalDateTime scheduleDate, char operator) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            boolean hasMatchingScheduleDate = false;
            for (Hearing h : cp.get(i).getHearingList()) {
                if (compare(h.getScheduleDate(), scheduleDate, operator)) {
                    hasMatchingScheduleDate = true;
                    break;
                }
            }
            if (!hasMatchingScheduleDate) {
                cp.remove(i);
            }
        }
    }

    private void filterByHearingOutcome(List<ContractorPerformance> cp, String outcome) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            boolean hasMatchingOutcome = false;
            for (Hearing h : cp.get(i).getHearingList()) {
                if (h.getOutcome().name().equalsIgnoreCase(outcome)) {
                    hasMatchingOutcome = true;
                    break;
                }
            }
            if (!hasMatchingOutcome) {
                cp.remove(i);
            }
        }
    }

    private void filterByUserGender(List<ContractorPerformance> cp, String gender) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            if (!cp.get(i).getUser().getGender().equalsIgnoreCase(gender)) {
                cp.remove(i);
            }
        }
    }

    private void filterByUserRace(List<ContractorPerformance> cp, String race) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            if (!cp.get(i).getUser().getRace().equalsIgnoreCase(race)) {
                cp.remove(i);
            }
        }
    }

    private void filterByUserAge(List<ContractorPerformance> cp, int age, char operator) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            if (!compare(cp.get(i).getUser().getAge(), age, operator)) {
                cp.remove(i);
            }
        }
    }

    private void filterByWarningDateIssued(List<ContractorPerformance> cp, LocalDateTime dateIssued, char operator) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            boolean hasMatchingWarningDate = false;
            for (Warning w : cp.get(i).getWarningList()) {
                if (compare(w.getDateIssue(), dateIssued, operator)) {
                    hasMatchingWarningDate = true;
                    break;
                }
            }
            if (!hasMatchingWarningDate) {
                cp.remove(i);
            }
        }
    }

    private void filterByWarningReason(List<ContractorPerformance> cp, String reason) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            boolean hasMatchingReason = false;
            for (Warning w : cp.get(i).getWarningList()) {
                if (w.getReason().name().equalsIgnoreCase(reason)) {
                    hasMatchingReason = true;
                    break;
                }
            }
            if (!hasMatchingReason) {
                cp.remove(i);
            }
        }
    }

    private void filterByWarningState(List<ContractorPerformance> cp, String state) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            boolean hasMatchingState = false;
            for (Warning w : cp.get(i).getWarningList()) {
                if (w.getState().name().equalsIgnoreCase(state)) {
                    hasMatchingState = true;
                    break;
                }
            }
            if (!hasMatchingState) {
                cp.remove(i);
            }
        }
    }

    private void filterByAptitudeTestMark(List<ContractorPerformance> cp, int mark, char operator) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            AptitudeTest aptitudeTest = cp.get(i).getAptitudeTest();
            if (aptitudeTest == null || !compare(aptitudeTest.getTestMark(), mark, operator)) {
                cp.remove(i);
            }
        }
    }

    private void filterByAptitudeTestDate(List<ContractorPerformance> cp, LocalDateTime testDate, char operator) {
        for (int i = cp.size() - 1; i >= 0; i--) {
            AptitudeTest aptitudeTest = cp.get(i).getAptitudeTest();
            if (aptitudeTest == null || !compare(aptitudeTest.getTestDate(), testDate, operator)) {
                cp.remove(i);
            }
        }
    }

// Comparison utility method
    private <T extends Comparable<T>> boolean compare(T actual, T target, char operator) {
        switch (operator) {
            case '=':
                return actual.compareTo(target) == 0;
            case '>':
                return actual.compareTo(target) > 0;
            case '<':
                return actual.compareTo(target) < 0;
            default:
                return false;
        }

    }

    @Override
    public List<ContractorPerformance> getAllContractors() throws SQLException {
        String query = "SELECT "
                + "user.user_id, user.name AS user_name, user.surname, user.email, user.age, user.gender, user.race, "
                + "contractor.contractor_id, contractor.status "
                + "FROM user "
                + "JOIN contractor ON user.user_id = contractor.user_id";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            List<ContractorPerformance> cpList = new ArrayList<>();  // Initialize list outside the loop
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Create new ContractorPerformance object for each result
                    ContractorPerformance cp = new ContractorPerformance();

                    // Populate User
                    User user = User.builder()
                            .userId(rs.getLong("user_id"))
                            .name(rs.getString("user_name"))
                            .surname(rs.getString("surname"))
                            .email(rs.getString("email"))
                            .age(rs.getInt("age"))
                            .race(rs.getString("race"))
                            .gender(rs.getString("gender"))
                            .build();

                    // Populate Contractor
                    Contractor contractor = Contractor.builder()
                            .contractorId(rs.getLong("contractor_id"))
                            .status(Contractor.Status.valueOf(rs.getString("status"))) // Ensure "status" is a valid enum value
                            .build();

                    // Set the user and contractor to the contractor performance object
                    cp.setUser(user);
                    cp.setContractor(contractor);

                    // Add ContractorPerformance to the list
                    cpList.add(cp);
                }

            }
            return cpList;  // Return the list of contractor performances
            }
            return cpList;  // Return the list of contractor performances
        }
    }

    @Override
    public String downloadReportFile(List<ContractorPerformance> contractorPerformanceList) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            CreationHelper creationHelper = workbook.getCreationHelper();

            // Define a cell style for date and time
            CellStyle dateTimeStyle = workbook.createCellStyle();
            dateTimeStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

            // Define a cell style for date only
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy"));

            // Users Sheet
            Sheet userSheet = workbook.createSheet("User details");
            Row userHeaderRow = userSheet.createRow(0);
            userHeaderRow.createCell(0).setCellValue("Name");
            userHeaderRow.createCell(1).setCellValue("Surname");
            userHeaderRow.createCell(2).setCellValue("Email");
            userHeaderRow.createCell(3).setCellValue("Age");
            userHeaderRow.createCell(4).setCellValue("Gender");
            userHeaderRow.createCell(5).setCellValue("Race");
            userHeaderRow.createCell(6).setCellValue("Status");
            userHeaderRow.createCell(7).setCellValue("Contract Period");
            userHeaderRow.createCell(8).setCellValue("Start date");
            userHeaderRow.createCell(9).setCellValue("End date");
            int userRowNum = 1;

            for (ContractorPerformance cp : contractorPerformanceList) {
                Row row = userSheet.createRow(userRowNum++);
                row.createCell(0).setCellValue(cp.getUser().getName());
                row.createCell(1).setCellValue(cp.getUser().getSurname());
                row.createCell(2).setCellValue(cp.getUser().getEmail());
                row.createCell(3).setCellValue(cp.getUser().getAge());
                row.createCell(4).setCellValue(cp.getUser().getGender());
                row.createCell(5).setCellValue(cp.getUser().getRace());
                row.createCell(6).setCellValue(cp.getContractor().getStatus().toString());
                row.createCell(7).setCellValue(cp.getContractPeriod().getName());
                Cell startDateCell = row.createCell(8);
                startDateCell.setCellValue(cp.getContractPeriod().getStartDate());
                startDateCell.setCellStyle(dateStyle); // Format as date
                Cell endDateCell = row.createCell(9);
                endDateCell.setCellValue(cp.getContractPeriod().getEndDate());
                endDateCell.setCellStyle(dateStyle); // Format as date
            }

            // Attendance Sheet
            Sheet attendanceSheet = workbook.createSheet("Attendance list");
            Row attHeaderRow = attendanceSheet.createRow(0);
            attHeaderRow.createCell(0).setCellValue("User full name");
            attHeaderRow.createCell(1).setCellValue("Time-in");
            attHeaderRow.createCell(2).setCellValue("Time-out");
            attHeaderRow.createCell(3).setCellValue("Register");
            int attRowNum = 1;

            for (ContractorPerformance cp : contractorPerformanceList) {
                for (Attendance att : cp.getAttendanceList()) {
                    Row row = attendanceSheet.createRow(attRowNum++);
                    row.createCell(0).setCellValue(cp.getUser().getName() + " " + cp.getUser().getSurname());
                    Cell timeInCell = row.createCell(1);
                    timeInCell.setCellValue(att.getTimeIn());
                    timeInCell.setCellStyle(dateTimeStyle); // Format as date with time
                    Cell timeOutCell = row.createCell(2);
                    timeOutCell.setCellValue(att.getTimeOut());
                    timeOutCell.setCellStyle(dateTimeStyle); // Format as date with time
                    row.createCell(3).setCellValue(att.getRegister().toString());
                }
            }

            // Hearings Sheet
            Sheet hearingSheet = workbook.createSheet("Hearings");
            Row hearHeaderRow = hearingSheet.createRow(0);
            hearHeaderRow.createCell(0).setCellValue("User full name");
            hearHeaderRow.createCell(1).setCellValue("Schedule date");
            hearHeaderRow.createCell(2).setCellValue("Reason");
            hearHeaderRow.createCell(3).setCellValue("Outcome");
            int hearingRowNum = 1;

            for (ContractorPerformance cp : contractorPerformanceList) {
                for (Hearing h : cp.getHearingList()) {
                    Row row = hearingSheet.createRow(hearingRowNum++);
                    row.createCell(0).setCellValue(cp.getUser().getName() + " " + cp.getUser().getSurname());
                    Cell scheduleDateCell = row.createCell(1);
                    scheduleDateCell.setCellValue(h.getScheduleDate());
                    scheduleDateCell.setCellStyle(dateTimeStyle); // Format as date with time
                    row.createCell(2).setCellValue(h.getReason());
                    row.createCell(3).setCellValue(h.getOutcome().toString());
                }
            }

            // Warnings Sheet
            Sheet warningSheet = workbook.createSheet("Warnings");
            Row warningHeaderRow = warningSheet.createRow(0);
            warningHeaderRow.createCell(0).setCellValue("User full name");
            warningHeaderRow.createCell(1).setCellValue("Date issued");
            warningHeaderRow.createCell(2).setCellValue("Reason");
            warningHeaderRow.createCell(3).setCellValue("State");
            int warningRowNum = 1;

            for (ContractorPerformance cp : contractorPerformanceList) {
                for (Warning w : cp.getWarningList()) {
                    Row row = warningSheet.createRow(warningRowNum++);
                    row.createCell(0).setCellValue(cp.getUser().getName() + " " + cp.getUser().getSurname());
                    Cell dateIssueCell = row.createCell(1);
                    dateIssueCell.setCellValue(w.getDateIssue());
                    dateIssueCell.setCellStyle(dateStyle); // Format as date only
                    row.createCell(2).setCellValue(w.getReason().toString());
                    row.createCell(3).setCellValue(w.getState().toString());
                }
            }

            // Aptitude Tests Sheet
            Sheet aptSheet = workbook.createSheet("Aptitude tests");
            Row aptHeaderRow = aptSheet.createRow(0);
            aptHeaderRow.createCell(0).setCellValue("User full name");
            aptHeaderRow.createCell(1).setCellValue("Test mark");
            aptHeaderRow.createCell(2).setCellValue("Test date");
            int aptRowNum = 1;

            for (ContractorPerformance cp : contractorPerformanceList) {
                Row row = aptSheet.createRow(aptRowNum++);
                row.createCell(0).setCellValue(cp.getUser().getName() + " " + cp.getUser().getSurname());
                row.createCell(1).setCellValue(cp.getAptitudeTest().getTestMark());
                Cell testDateCell = row.createCell(2);
                testDateCell.setCellValue(cp.getAptitudeTest().getTestDate());
                testDateCell.setCellStyle(dateStyle); // Format as date only
            }

            //Contractors by age range
            Map<String, Integer> contractorsByAgeRange = new HashMap<>();
            //Contractors by gender
            Map<String, Integer> contractorsByGender = new HashMap<>();
            //Contractors by race
            Map<String, Integer> contractorsByRace = new HashMap<>();
            //Contractor by status
            Map<String, Integer> contractorsByStatus = new HashMap<>();

            // *** WARNINGS BY GENDER, RACE, AND AGE AGGREGATION ***
            // The attributes are represented by keys, the values represent the counts of each.
            Map<String, Integer> warningsByGender = new HashMap<>();
            Map<String, Integer> warningsByRace = new HashMap<>();
            Map<String, Integer> warningsByAgeRange = new HashMap<>();

            //HEARINGS BY GENDER, RACE, AND AGE AGGREGATION
            Map<String, Integer> hearingsByGender = new HashMap<>();
            Map<String, Integer> hearingsByRace = new HashMap<>();
            Map<String, Integer> hearingsByAgeRange = new HashMap<>();

            //ATTENDANCE BY GENDER, RACE AND AGE AGGREGATION
            Map<String, Integer> attendanceByGender = new HashMap<>();
            Map<String, Integer> attendanceByRace = new HashMap<>();
            Map<String, Integer> attendanceByAgeRange = new HashMap<>();

            // New Maps for tracking counts of females and males with warnings/hearings
            Map<String, Integer> femalesWithWarnings = new HashMap<>();
            Map<String, Integer> malesWithWarnings = new HashMap<>();
            Map<String, Integer> femalesWithHearings = new HashMap<>();
            Map<String, Integer> malesWithHearings = new HashMap<>();

            // Initialize totals for gender count (unchanged)
            Map<String, Integer> totalContractorsByGender = new HashMap<>();
            totalContractorsByGender.put("Female", 0);
            totalContractorsByGender.put("Male", 0);

            for (ContractorPerformance cp : contractorPerformanceList) {
                String gender = cp.getUser().getGender();
                String race = cp.getUser().getRace();
                int age = cp.getUser().getAge();
                String status = cp.getContractor().getStatus().toString();

                String ageRange = age < 20 ? "Under 20"
                        : age < 25 ? "20-24"
                                : age < 30 ? "25-29"
                                        : age < 35 ? "30-34"
                                                : age < 40 ? "35-39" : "40+";

                //Contractors by age range
                contractorsByAgeRange.put(ageRange, contractorsByAgeRange.getOrDefault(ageRange, 0) + 1);
                //Contractors by gender
                contractorsByGender.put(gender, contractorsByGender.getOrDefault(gender, 0) + 1);
                //Contractors by race
                contractorsByRace.put(race, contractorsByRace.getOrDefault(race, 0) + 1);
                //Contractor by status
                contractorsByStatus.put(status, contractorsByStatus.getOrDefault(status, 0) + 1);

                int warningCount = cp.getWarningList().size();
                warningsByGender.put(gender, warningsByGender.getOrDefault(gender, 0) + warningCount);
                warningsByRace.put(race, warningsByRace.getOrDefault(race, 0) + warningCount);
                warningsByAgeRange.put(ageRange, warningsByAgeRange.getOrDefault(ageRange, 0) + warningCount);

                // Track warnings for gender (new logic)
                if (warningCount > 0) {
                    if ("Female".equalsIgnoreCase(gender)) {
                        femalesWithWarnings.put("Warnings", femalesWithWarnings.getOrDefault("Warnings", 0) + 1);
                    } else if ("Male".equalsIgnoreCase(gender)) {
                        malesWithWarnings.put("Warnings", malesWithWarnings.getOrDefault("Warnings", 0) + 1);
                    }
                }

                int hearingCount = cp.getHearingList().size();
                hearingsByGender.put(gender, hearingsByGender.getOrDefault(gender, 0) + hearingCount);
                hearingsByRace.put(race, hearingsByRace.getOrDefault(race, 0) + hearingCount);
                hearingsByAgeRange.put(ageRange, hearingsByAgeRange.getOrDefault(ageRange, 0) + hearingCount);

                // Track hearings for gender (new logic)
                if (hearingCount > 0) {
                    if ("Female".equalsIgnoreCase(gender)) {
                        femalesWithHearings.put("Hearings", femalesWithHearings.getOrDefault("Hearings", 0) + 1);
                    } else if ("Male".equalsIgnoreCase(gender)) {
                        malesWithHearings.put("Hearings", malesWithHearings.getOrDefault("Hearings", 0) + 1);
                    }
                }

                int attendanceCount = cp.getAttendanceList().size();
                attendanceByGender.put(gender, attendanceByGender.getOrDefault(gender, 0) + attendanceCount);
                attendanceByRace.put(race, attendanceByRace.getOrDefault(race, 0) + attendanceCount);
                attendanceByAgeRange.put(ageRange, attendanceByAgeRange.getOrDefault(ageRange, 0) + attendanceCount);
            }

            // Calculate the percentage of females and males with warnings and hearings (outside the loop)
            double percentageFemalesWithWarnings = 0;
            double percentageMalesWithWarnings = 0;
            double percentageFemalesWithHearings = 0;
            double percentageMalesWithHearings = 0;

            // Calculate the percentage of females and males with warnings and hearings (outside the loop)
            if (totalContractorsByGender.get("Female") > 0) {
                percentageFemalesWithWarnings = (double) femalesWithWarnings.get("Warnings") / totalContractorsByGender.get("Female") * 100;
                percentageFemalesWithHearings = (double) femalesWithHearings.get("Hearings") / totalContractorsByGender.get("Female") * 100;
            }

            if (totalContractorsByGender.get("Male") > 0) {
                percentageMalesWithWarnings = (double) malesWithWarnings.get("Warnings") / totalContractorsByGender.get("Male") * 100;
                percentageMalesWithHearings = (double) malesWithHearings.get("Hearings") / totalContractorsByGender.get("Male") * 100;
            }

            // *** AGGREGATION OF CONTRACTORS BY AGE RANGE ***
// * SUMMARY SHEET *
            Sheet summarySheet = workbook.createSheet("Warnings Summary");
            Row summaryHeader = summarySheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Category");
            summaryHeader.createCell(1).setCellValue("Count");

            int summaryRowNum = 1;
// Add Warnings by Gender
            Row genderHeader = summarySheet.createRow(summaryRowNum++);
            genderHeader.createCell(0).setCellValue("Warnings by Gender");
            for (Map.Entry<String, Integer> entry : warningsByGender.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Add Warnings by Race
            Row raceHeader = summarySheet.createRow(summaryRowNum++);
            raceHeader.createCell(0).setCellValue("Warnings by Race");
            for (Map.Entry<String, Integer> entry : warningsByRace.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Add Warnings by Age Range
            Row ageHeader = summarySheet.createRow(summaryRowNum++);
            ageHeader.createCell(0).setCellValue("Warnings by Age Range");
            for (Map.Entry<String, Integer> entry : warningsByAgeRange.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            // Add Warnings by Females
            Row femaleHeader = summarySheet.createRow(summaryRowNum++);
            femaleHeader.createCell(0).setCellValue("Warnings by Females");
            for (Map.Entry<String, Integer> entry : femalesWithWarnings.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Add the percentage of females with warnings to the Excel sheet (below the warnings data)
            Row femalePercentageRow = summarySheet.createRow(summaryRowNum++);
            femalePercentageRow.createCell(0).setCellValue("Percentage of Females with Warnings");
            femalePercentageRow.createCell(1).setCellValue(percentageFemalesWithWarnings + "%");

// Add Warnings by Males
            Row maleHeader = summarySheet.createRow(summaryRowNum++);
            maleHeader.createCell(0).setCellValue("Warnings by Males");
            for (Map.Entry<String, Integer> entry : malesWithWarnings.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Add the percentage of males with warnings to the Excel sheet (below the warnings data)
            Row malePercentageRow = summarySheet.createRow(summaryRowNum++);
            malePercentageRow.createCell(0).setCellValue("Percentage of Males with Warnings");
            malePercentageRow.createCell(1).setCellValue(percentageMalesWithWarnings + "%");

// * CREATING GRAPHS *
            XSSFDrawing drawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

// Gender Pie Chart
            XSSFClientAnchor genderAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
            XSSFChart genderChart = drawing.createChart(genderAnchor);
            genderChart.setTitleText("Warnings by Gender");
            genderChart.setTitleOverlay(false);
            genderChart.getOrAddLegend();
            XDDFDataSource<String> genderCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, 2 + warningsByGender.size() - 1, 0, 0));
            XDDFNumericalDataSource<Double> genderValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, 2 + warningsByGender.size() - 1, 1, 1));
            XDDFChartData genderData = genderChart.createData(ChartTypes.PIE, null, null);
            XDDFChartData.Series genderSeries = genderData.addSeries(genderCategories, genderValues);
            genderChart.plot(genderData);

// Race Pie Chart
            XSSFClientAnchor raceAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42);
            XSSFChart raceChart = drawing.createChart(raceAnchor);
            raceChart.setTitleText("Warnings by Race");
            raceChart.setTitleOverlay(false);
            raceChart.getOrAddLegend();
            XDDFDataSource<String> raceCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(3 + warningsByGender.size(), 3 + warningsByGender.size() + warningsByRace.size() - 1, 0, 0));
            XDDFNumericalDataSource<Double> raceValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(3 + warningsByGender.size(), 3 + warningsByGender.size() + warningsByRace.size() - 1, 1, 1));
            XDDFChartData raceData = raceChart.createData(ChartTypes.PIE, null, null);
            XDDFChartData.Series raceSeries = raceData.addSeries(raceCategories, raceValues);
            raceChart.plot(raceData);

            // *** CREATING BAR CHART FOR CONTRACTORS BY AGE RANGE ***
            XSSFDrawing barDrawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

            // Age Range Bar Chart
            XSSFClientAnchor ageAnchor = barDrawing.createAnchor(0, 0, 0, 0, 16, 1, 26, 21);
            XSSFChart ageChart = barDrawing.createChart(ageAnchor);
            ageChart.setTitleText("Contractors by Age Range");
            ageChart.getOrAddLegend();

            // Create the category axis (X-axis)
            XDDFCategoryAxis categoryAxis = (XDDFCategoryAxis) ageChart.createCategoryAxis(AxisPosition.LEFT);
            categoryAxis.setTitle("Age Range");

            // Set the data for the category axis (age ranges)
            XDDFDataSource<String> ageCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + warningsByGender.size() + 2 + warningsByRace.size(), 2 + warningsByGender.size() + 2 + warningsByRace.size() + warningsByAgeRange.size() - 1, 0, 0));

            // Create the value axis (Y-axis)
            XDDFValueAxis valueAxis = (XDDFValueAxis) ageChart.createValueAxis(AxisPosition.BOTTOM);
            valueAxis.setTitle("Contractor Count");

            // Set the data for the value axis (contractor counts)
            XDDFNumericalDataSource<Double> ageValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + warningsByGender.size() + 2 + warningsByRace.size(), 2 + warningsByGender.size() + 2 + warningsByRace.size() + warningsByAgeRange.size() - 1, 1, 1));

            // Create chart data for the bar chart
            XDDFBarChartData ageData = (XDDFBarChartData) ageChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            ageData.setBarDirection(BarDirection.COL);
            ageData.setGapWidth(150);
            ageData.setOverlap((byte) 0);
            // Add the data series to the chart
            XDDFChartData.Series ageSeries = ageData.addSeries(ageCategories, ageValues);

            // Customize the series (e.g., set bar color or width if needed)
            ageSeries.setTitle("Contractors by Age Range", null);

            // Plot the chart with both axes and data
            ageChart.plot(ageData);

            //-------------------------------------------------------------------------------------------------------------------------
            // * SUMMARY SHEET *
            summarySheet = workbook.createSheet("Hearing Summary");
            summaryHeader = summarySheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Category");
            summaryHeader.createCell(1).setCellValue("Count");

            summaryRowNum = 1;
// Add Hearings by Gender
            genderHeader = summarySheet.createRow(summaryRowNum++);
            genderHeader.createCell(0).setCellValue("Hearings by Gender");
            for (Map.Entry<String, Integer> entry : hearingsByGender.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Add Hearings by Race
            raceHeader = summarySheet.createRow(summaryRowNum++);
            raceHeader.createCell(0).setCellValue("Hearings by Race");
            for (Map.Entry<String, Integer> entry : hearingsByRace.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Add Hearings by Age Range
            ageHeader = summarySheet.createRow(summaryRowNum++);
            ageHeader.createCell(0).setCellValue("Hearings by Age Range");
            for (Map.Entry<String, Integer> entry : hearingsByAgeRange.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// * CREATING GRAPHS *
            drawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

// Gender Pie Chart
            genderAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
            genderChart = drawing.createChart(genderAnchor);
            genderChart.setTitleText("Hearings by Gender");
            genderChart.setTitleOverlay(false);
            genderChart.getOrAddLegend();
            genderCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, 2 + hearingsByGender.size() - 1, 0, 0));
            genderValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, 2 + hearingsByGender.size() - 1, 1, 1));
            genderData = genderChart.createData(ChartTypes.PIE, null, null);
            genderSeries = genderData.addSeries(genderCategories, genderValues);
            genderChart.plot(genderData);

// Race Pie Chart
            raceAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42);
            raceChart = drawing.createChart(raceAnchor);
            raceChart.setTitleText("Hearings by Race");
            raceChart.setTitleOverlay(false);
            raceChart.getOrAddLegend();
            raceCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(3 + hearingsByGender.size(), 3 + hearingsByGender.size() + hearingsByRace.size() - 1, 0, 0));
            raceValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(3 + hearingsByGender.size(), 3 + hearingsByGender.size() + hearingsByRace.size() - 1, 1, 1));
            raceData = raceChart.createData(ChartTypes.PIE, null, null);
            raceSeries = raceData.addSeries(raceCategories, raceValues);
            raceChart.plot(raceData);

            // *** CREATING BAR CHART FOR CONTRACTORS BY AGE RANGE ***
            barDrawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

            // Age Range Bar Chart
            ageAnchor = barDrawing.createAnchor(0, 0, 0, 0, 16, 1, 26, 21);
            ageChart = barDrawing.createChart(ageAnchor);
            ageChart.setTitleText("Contractors by Age Range");
            ageChart.getOrAddLegend();

            // Create the category axis (X-axis)
            categoryAxis = (XDDFCategoryAxis) ageChart.createCategoryAxis(AxisPosition.LEFT);
            categoryAxis.setTitle("Age Range");

            // Set the data for the category axis (age ranges)
            ageCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + hearingsByGender.size() + 2 + hearingsByRace.size(), 2 + hearingsByGender.size() + 2 + hearingsByRace.size() + hearingsByAgeRange.size() - 1, 0, 0));

            // Create the value axis (Y-axis)
            valueAxis = (XDDFValueAxis) ageChart.createValueAxis(AxisPosition.BOTTOM);
            valueAxis.setTitle("Contractor Count");

            // Set the data for the value axis (contractor counts)
            ageValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + hearingsByGender.size() + 2 + hearingsByRace.size(), 2 + hearingsByGender.size() + 2 + hearingsByRace.size() + hearingsByAgeRange.size() - 1, 1, 1));

            // Create chart data for the bar chart
            ageData = (XDDFBarChartData) ageChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            ageData.setBarDirection(BarDirection.COL);
            ageData.setGapWidth(150);
            ageData.setOverlap((byte) 0);
            // Add the data series to the chart
            ageSeries = ageData.addSeries(ageCategories, ageValues);

            // Customize the series (e.g., set bar color or width if needed)
            ageSeries.setTitle("Contractors by Age Range", null);

            // Plot the chart with both axes and data
            ageChart.plot(ageData);

            //-------------------------------------------------------------------------------------------------------------------------
            // * SUMMARY SHEET *
            summarySheet = workbook.createSheet("Attendance Summary");
            summaryHeader = summarySheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Category");
            summaryHeader.createCell(1).setCellValue("Count");

            summaryRowNum = 1;
// Add Attendance by Gender
            genderHeader = summarySheet.createRow(summaryRowNum++);
            genderHeader.createCell(0).setCellValue("Attendance by Gender");
            for (Map.Entry<String, Integer> entry : attendanceByGender.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Add Attendance by Race
            raceHeader = summarySheet.createRow(summaryRowNum++);
            raceHeader.createCell(0).setCellValue("Attendance by Race");
            for (Map.Entry<String, Integer> entry : attendanceByRace.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Add Attendance by Age Range
            ageHeader = summarySheet.createRow(summaryRowNum++);
            ageHeader.createCell(0).setCellValue("attendance by Age Range");
            for (Map.Entry<String, Integer> entry : attendanceByAgeRange.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// * CREATING GRAPHS *
            drawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

// Gender Pie Chart
            genderAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
            genderChart = drawing.createChart(genderAnchor);
            genderChart.setTitleText("Hearings by Gender");
            genderChart.setTitleOverlay(false);
            genderChart.getOrAddLegend();
            genderCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, 2 + attendanceByGender.size() - 1, 0, 0));
            genderValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, 2 + attendanceByGender.size() - 1, 1, 1));
            genderData = genderChart.createData(ChartTypes.PIE, null, null);
            genderSeries = genderData.addSeries(genderCategories, genderValues);
            genderChart.plot(genderData);

// Race Pie Chart
            raceAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42);
            raceChart = drawing.createChart(raceAnchor);
            raceChart.setTitleText("Attendance by Race");
            raceChart.setTitleOverlay(false);
            raceChart.getOrAddLegend();
            raceCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(3 + attendanceByGender.size(), 3 + attendanceByGender.size() + attendanceByRace.size() - 1, 0, 0));
            raceValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(3 + attendanceByGender.size(), 3 + attendanceByGender.size() + attendanceByRace.size() - 1, 1, 1));
            raceData = raceChart.createData(ChartTypes.PIE, null, null);
            raceSeries = raceData.addSeries(raceCategories, raceValues);
            raceChart.plot(raceData);

            // *** CREATING BAR CHART FOR CONTRACTORS BY AGE RANGE ***
            barDrawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

            // Age Range Bar Chart
            ageAnchor = barDrawing.createAnchor(0, 0, 0, 0, 16, 1, 26, 21);
            ageChart = barDrawing.createChart(ageAnchor);
            ageChart.setTitleText("Contractors by Age Range");
            ageChart.getOrAddLegend();

            // Create the category axis (X-axis)
            categoryAxis = (XDDFCategoryAxis) ageChart.createCategoryAxis(AxisPosition.LEFT);
            categoryAxis.setTitle("Age Range");

            // Set the data for the category axis (age ranges)
            ageCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + attendanceByGender.size() + 2 + attendanceByRace.size(), 2 + attendanceByGender.size() + 2 + attendanceByRace.size() + attendanceByAgeRange.size() - 1, 0, 0));

            // Create the value axis (Y-axis)
            valueAxis = (XDDFValueAxis) ageChart.createValueAxis(AxisPosition.BOTTOM);
            valueAxis.setTitle("Contractor Count");

            // Set the data for the value axis (contractor counts)
            ageValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2 + attendanceByGender.size() + 2 + attendanceByRace.size(), 2 + attendanceByGender.size() + 2 + attendanceByRace.size() + attendanceByAgeRange.size() - 1, 1, 1));

            // Create chart data for the bar chart
            ageData = (XDDFBarChartData) ageChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            ageData.setBarDirection(BarDirection.COL);
            ageData.setGapWidth(150);
            ageData.setOverlap((byte) 0);
            // Add the data series to the chart
            ageSeries = ageData.addSeries(ageCategories, ageValues);

            // Customize the series (e.g., set bar color or width if needed)
            ageSeries.setTitle("Contractors by Age Range", null);

            // Plot the chart with both axes and data
            ageChart.plot(ageData);

            //-------------------------------------------------------------------------------------------------------------------------
            // * SUMMARY SHEET *
            summarySheet = workbook.createSheet("Contractors Summary");
            summaryHeader = summarySheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Category");
            summaryHeader.createCell(1).setCellValue("Count");

            summaryRowNum = 1;
// Contractors by Gender
            genderHeader = summarySheet.createRow(summaryRowNum++);
            genderHeader.createCell(0).setCellValue("Contractors Gender");
            for (Map.Entry<String, Integer> entry : contractorsByGender.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Contractors by Race
            raceHeader = summarySheet.createRow(summaryRowNum++);
            raceHeader.createCell(0).setCellValue("Contractors by Race");
            for (Map.Entry<String, Integer> entry : contractorsByRace.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Contractors by status
            raceHeader = summarySheet.createRow(summaryRowNum++);
            raceHeader.createCell(0).setCellValue("Contractors by status");
            for (Map.Entry<String, Integer> entry : contractorsByStatus.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// Contractors by Age Range
            ageHeader = summarySheet.createRow(summaryRowNum++);
            ageHeader.createCell(0).setCellValue("Contractos by Age Range");
            for (Map.Entry<String, Integer> entry : contractorsByAgeRange.entrySet()) {
                Row row = summarySheet.createRow(summaryRowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

// * CREATING GRAPHS *
            drawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

// Gender Pie Chart
            genderAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 21);
            genderChart = drawing.createChart(genderAnchor);
            genderChart.setTitleText("contractors by Gender");
            genderChart.setTitleOverlay(false);
            genderChart.getOrAddLegend();
            genderCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, 2 + contractorsByGender.size() - 1, 0, 0));
            genderValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(2, 2 + contractorsByGender.size() - 1, 1, 1));
            genderData = genderChart.createData(ChartTypes.PIE, null, null);
            genderSeries = genderData.addSeries(genderCategories, genderValues);
            genderChart.plot(genderData);

// Race Pie Chart
            raceAnchor = drawing.createAnchor(0, 0, 0, 0, 5, 22, 15, 42);
            raceChart = drawing.createChart(raceAnchor);
            raceChart.setTitleText("contractors by Race");
            raceChart.setTitleOverlay(false);
            raceChart.getOrAddLegend();
            raceCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(3 + contractorsByGender.size(), 3 + contractorsByGender.size() + contractorsByRace.size() - 1, 0, 0));
            raceValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(3 + contractorsByGender.size(), 3 + contractorsByGender.size() + contractorsByRace.size() - 1, 1, 1));
            raceData = raceChart.createData(ChartTypes.PIE, null, null);
            raceSeries = raceData.addSeries(raceCategories, raceValues);
            raceChart.plot(raceData);

// Status Bar Chart
            XSSFClientAnchor statusAnchor = drawing.createAnchor(0, 0, 0, 0, 16, 22, 26, 42);
            XSSFChart statusChart = drawing.createChart(statusAnchor);
            statusChart.setTitleText("Contractors by Status");
            statusChart.setTitleOverlay(false);
            statusChart.getOrAddLegend();

// Define axes for the bar chart
            categoryAxis = statusChart.createCategoryAxis(AxisPosition.BOTTOM);
            categoryAxis.setTitle("Status");
            valueAxis = statusChart.createValueAxis(AxisPosition.LEFT);
            valueAxis.setTitle("Number of Contractors");

// Create data sources from the summary sheet
            XDDFDataSource<String> statusCategories = XDDFDataSourcesFactory.fromStringCellRange(
                    (XSSFSheet) summarySheet,
                    new CellRangeAddress(10, 10 + contractorsByStatus.size() - 1, 0, 0) // Adjust row range for status
            );
            XDDFNumericalDataSource<Double> statusValues = XDDFDataSourcesFactory.fromNumericCellRange(
                    (XSSFSheet) summarySheet,
                    new CellRangeAddress(10, 10 + contractorsByStatus.size() - 1, 1, 1) // Adjust column range for values
            );

// Create the bar chart data
            XDDFChartData barData = statusChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            XDDFChartData.Series barSeries = barData.addSeries(statusCategories, statusValues);
            barSeries.setTitle("Contractors", null); // Add a title for the series

// Plot the chart with the data
            statusChart.plot(barData);

// Adjust bar chart direction (vertical instead of horizontal)
            ((XDDFBarChartData) barData).setBarDirection(BarDirection.COL);

            // *** CREATING BAR CHART FOR CONTRACTORS BY AGE RANGE ***
            barDrawing = (XSSFDrawing) summarySheet.createDrawingPatriarch();

            // Age Range Bar Chart
            ageAnchor = barDrawing.createAnchor(0, 0, 0, 0, 16, 1, 26, 21);
            ageChart = barDrawing.createChart(ageAnchor);
            ageChart.setTitleText("Contractors by Age Range");
            ageChart.getOrAddLegend();

            // Create the category axis (X-axis)
            categoryAxis = (XDDFCategoryAxis) ageChart.createCategoryAxis(AxisPosition.LEFT);
            categoryAxis.setTitle("Age Range");

            // Set the data for the category axis (age ranges)
            ageCategories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(15, 15 + contractorsByAgeRange.size() - 1, 0, 0));

            // Create the value axis (Y-axis)
            valueAxis = (XDDFValueAxis) ageChart.createValueAxis(AxisPosition.BOTTOM);
            valueAxis.setTitle("Contractor Count");

            // Set the data for the value axis (contractor counts)
            ageValues = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) summarySheet,
                    new CellRangeAddress(15, 15 + contractorsByAgeRange.size() - 1, 1, 1));

            // Create chart data for the bar chart
            ageData = (XDDFBarChartData) ageChart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            ageData.setBarDirection(BarDirection.COL);
            ageData.setGapWidth(150);
            ageData.setOverlap((byte) 0);
            // Add the data series to the chart
            ageSeries = ageData.addSeries(ageCategories, ageValues);

            // Customize the series (e.g., set bar color or width if needed)
            ageSeries.setTitle("Contractors by Age Range", null);

            // Plot the chart with both axes and data
            ageChart.plot(ageData);

            File reportFile = new File("C:/Users/arshr/OneDrive/Documents/reports.xlsx");
            if (reportFile.exists() == false) {
                reportFile.createNewFile();
            }
            try (FileOutputStream fileOut = new FileOutputStream(reportFile)) {
                workbook.write(fileOut);
            }
            return reportFile.getPath();
        }
    }

}
