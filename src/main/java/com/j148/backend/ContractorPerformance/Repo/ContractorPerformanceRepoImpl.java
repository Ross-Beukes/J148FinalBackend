package com.j148.backend.ContractorPerformance.Repo;

import com.j148.backend.ContractorPerformance.model.ContractorPerformance;
import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.config.DBConfig;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.user.model.User;
import com.j148.backend.warning.model.Warning;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
                if (rs.next()) {
                    // Populate User information (only one user per contractor performance)
                    User dbUser = User.builder()
                            .userId(rs.getLong("user_id"))
                            .name(rs.getString("user_name"))
                            .surname(rs.getString("surname"))
                            .email(rs.getString("email"))
                            .build();
                    cp.setUser(dbUser);  // Set the singular user

                    // Populate Contractor information (only one contractor per contractor performance)
                    Contractor contractor = Contractor.builder()
                            .contractorId(rs.getLong("contractor_id"))
                            .status(Contractor.Status.valueOf(rs.getString("status")))
                            .build();
                    cp.setContractor(contractor);  // Set the singular contractor

                    // Populate ContractPeriod information (only one contract period per contractor performance)
                    ContractPeriod contractPeriod = ContractPeriod.builder()
                            .name(rs.getString("period_name"))
                            .startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate())
                            .build();
                    cp.setContractPeriod(contractPeriod);  // Set the singular contract period

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
            }
        }

        return Optional.of(cp);
    }

    @Override
    public List<ContractorPerformance> getAllContractorPerformance() throws SQLException {
        List<ContractorPerformance> contractorPerformances = new ArrayList<>();

        String query = "SELECT "
                + "user.user_id, user.name AS user_name, user.surname, user.email, "
                + "contractor.contractor_id, contractor.user_id AS contractor_user_id, contractor.status, contractor.contractor_period_id, "
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
                + "LEFT JOIN hearings ON contractor.contractor_id = hearings.contractor_id;";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ContractorPerformance cp = new ContractorPerformance();

                    // Populate User information (only one user per contractor performance)
                    User user = User.builder()
                            .userId(rs.getLong("user_id"))
                            .name(rs.getString("user_name"))
                            .surname(rs.getString("surname"))
                            .email(rs.getString("email"))
                            .build();
                    cp.setUser(user);  // Set the singular user

                    // Populate Contractor information
                    Contractor contractor = Contractor.builder()
                            .contractorId(rs.getLong("contractor_id"))
                            .user(User.builder().userId(rs.getLong("contractor_user_id")).build()) // Associate contractor with a user
                            .status(Contractor.Status.valueOf(rs.getString("status")))
                            .build();
                    cp.setContractor(contractor);  // Set the singular contractor

                    // Populate ContractPeriod information (only one contract period per contractor performance)
                    ContractPeriod contractPeriod = ContractPeriod.builder()
                            .name(rs.getString("period_name"))
                            .startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate())
                            .build();
                    cp.setContractPeriod(contractPeriod);  // Set the singular contract period

                    // Populate Warning information (if present)
                    if (rs.getTimestamp("date_issue") != null) {
                        Warning warning = Warning.builder()
                                .contractorId(rs.getLong("contractor_id"))
                                .dateIssue(rs.getTimestamp("date_issue").toLocalDateTime())
                                .reason(Warning.WarningReason.valueOf(rs.getString("warning_reason")))
                                .state(Warning.WarningState.valueOf(rs.getString("warning_state")))
                                .build();
                        cp.getWarningList().add(warning);  // Add warning to the list
                    }

                    // Populate Attendance information (if present)
                    if (rs.getTimestamp("time_in") != null) {
                        Attendance attendance = Attendance.builder()
                                .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
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
                                .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                                .scheduleDate(rs.getTimestamp("hearing_schedule_date").toLocalDateTime())
                                .outcome(Hearing.Outcome.valueOf(rs.getString("hearing_outcome")))
                                .reason(rs.getString("hearing_reason"))
                                .build();
                        cp.getHearingList().add(hearing);  // Add hearing to the list
                    }

                    // Add the filled ContractorPerformance to the list
                    contractorPerformances.add(cp);
                }
            }
        }

        return contractorPerformances;
    }
}
