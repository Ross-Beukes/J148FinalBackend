package com.j148.backend.ContractorPerformance.Repo;

import com.j148.backend.ContractorPerformance.model.ContractorPerformance;
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

public class ContractorPerformanceRepoImpl extends DBConfig implements ContractorPerformanceRepo {

    @Override
    public Optional<ContractorPerformance> getContractorPerformance(User user) throws SQLException {
        ContractorPerformance cp = ContractorPerformance.builder()
                .warningList(new ArrayList<>())
                .attendanceList(new ArrayList<>())
                .hearingList(new ArrayList<>())
                .build();

        String query = "SELECT "
                + "user.user_id, user.name AS user_name, user.surname, user.email, "
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
                + "user.user_id, user.name AS user_name, user.surname, user.email, "
                + "contractor.contractor_id, contractor.user_id AS contractor_user_id, contractor.status, contractor.contractor_period_id, "
                + "contractor_period.contractor_period_id, contractor_period.name AS period_name, "
                + "contractor_period.start_date, contractor_period.end_date, "
                + "warning.date_issue, warning.reason AS warning_reason, warning.state AS warning_state, "
                + "attendance.attendance_id, attendance.time_in, attendance.time_out, attendance.register AS attendance_register, "
                + "hearings.schedule_date AS hearing_schedule_date, hearings.outcome AS hearing_outcome, hearings.reason AS hearing_reason, "
                + "aptitude_test.aptitude_test_id, aptitude_test.test_mark, aptitude_test.test_date "
                + "FROM user "
                + "JOIN contractor ON user.user_id = contractor.user_id "
                + "JOIN contractor_period ON contractor.contractor_period_id = contractor_period.contractor_period_id "
                + "LEFT JOIN warning ON contractor.contractor_id = warning.contractor_id "
                + "LEFT JOIN attendance ON contractor.contractor_id = attendance.contractor_id "
                + "LEFT JOIN hearings ON contractor.contractor_id = hearings.contractor_id "
                + "LEFT JOIN aptitude_test ON user.user_id = aptitude_test.user_id;";

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
                                .build();
                        cp.setUser(user);

                        // Populate Contractor
                        Contractor contractor = Contractor.builder()
                                .contractorId(contractorId)
                                .user(User.builder().userId(rs.getLong("contractor_user_id")).build())
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

                        // Add to map for tracking
                        contractorMap.put(contractorId, cp);
                    }

                    // Populate Warning
                    if (rs.getTimestamp("date_issue") != null) {
                        Warning warning = Warning.builder()
                                .contractorId(contractorId)
                                .dateIssue(rs.getTimestamp("date_issue").toLocalDateTime())
                                .reason(Warning.WarningReason.valueOf(rs.getString("warning_reason")))
                                .state(Warning.WarningState.valueOf(rs.getString("warning_state")))
                                .build();
                        cp.getWarningList().add(warning);
                    }

                    // Populate Attendance
                    if (rs.getTimestamp("time_in") != null) {
                        Attendance attendance = Attendance.builder()
                                .contractor(Contractor.builder().contractorId(contractorId).build())
                                .attendanceId(rs.getLong("attendance_id"))
                                .timeIn(rs.getTimestamp("time_in").toLocalDateTime())
                                .timeOut(rs.getTimestamp("time_out").toLocalDateTime())
                                .register(Attendance.Register.valueOf(rs.getString("attendance_register")))
                                .build();
                        cp.getAttendanceList().add(attendance);
                    }

                    // Populate Hearing
                    if (rs.getTimestamp("hearing_schedule_date") != null) {
                        Hearing hearing = Hearing.builder()
                                .contractor(Contractor.builder().contractorId(contractorId).build())
                                .scheduleDate(rs.getTimestamp("hearing_schedule_date").toLocalDateTime())
                                .outcome(Hearing.Outcome.valueOf(rs.getString("hearing_outcome")))
                                .reason(rs.getString("hearing_reason"))
                                .build();
                        cp.getHearingList().add(hearing);
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
    public List<ContractorPerformance> filterContractorPerformance(String filters, List<ContractorPerformance> cp) {
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
}
