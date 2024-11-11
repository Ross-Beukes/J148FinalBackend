package com.j148.backend.warning.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.user.model.User;
import com.j148.backend.warning.model.Warning;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WarningRepoImpl extends DBConfig implements WarningRepo {

    @Override
    public Optional<Warning> save(Warning warning) throws SQLException {
        String sql = "INSERT INTO warning (contractor_id, date_issue, reason, state) VALUES (?, ?, ?, ?)";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            con.setAutoCommit(false);
            Savepoint beforeWarningSave = con.setSavepoint();

            try {
                ps.setLong(1, warning.getContractor().getContractorId());
                ps.setTimestamp(2, Timestamp.valueOf(warning.getDateIssue()));
                ps.setString(3, warning.getReason().toString());
                ps.setString(4, warning.getState().toString());

                if (ps.executeUpdate() > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            warning.setWarningId(generatedKeys.getLong(1));
                            con.commit();
                            return Optional.of(warning);
                        }
                    }
                }

                con.rollback(beforeWarningSave);
                return Optional.empty();

            } catch (SQLException e) {
                con.rollback(beforeWarningSave);
                throw e;
            }
        }
    }

    @Override
    public Optional<Warning> findById(Warning warning) throws SQLException {
        String sql = """
                SELECT w.*, c.*, u.*, cp.*
                FROM warning w 
                JOIN contractor c ON w.contractor_id = c.contractor_id 
                JOIN user u ON c.user_id = u.user_id 
                JOIN contractor_period cp ON c.contractor_period_id = cp.contractor_period_id
                WHERE w.warning_id = ?
                """;

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, warning.getWarningId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapWarningFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Warning>> findByContractor(Contractor contractor) throws SQLException {
        String sql = """
                SELECT w.*, c.*, u.*, cp.*
                FROM warning w 
                JOIN contractor c ON w.contractor_id = c.contractor_id 
                JOIN user u ON c.user_id = u.user_id 
                JOIN contractor_period cp ON c.contractor_period_id = cp.contractor_period_id
                WHERE w.contractor_id = ?
                """;

        List<Warning> warnings = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, contractor.getContractorId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    warnings.add(mapWarningFromResultSet(rs));
                }
            }
        }
        return Optional.of(warnings);
    }

    @Override
    public Optional<List<Warning>> findAllActiveByContractor(Contractor contractor) throws SQLException {
        String sql = """
                SELECT w.*, c.*, u.*, cp.*
                FROM warning w 
                JOIN contractor c ON w.contractor_id = c.contractor_id 
                JOIN user u ON c.user_id = u.user_id 
                JOIN contractor_period cp ON c.contractor_period_id = cp.contractor_period_id
                WHERE w.contractor_id = ? AND w.state = 'ACTIVE'
                """;

        List<Warning> warnings = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, contractor.getContractorId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    warnings.add(mapWarningFromResultSet(rs));
                }
            }
        }
        return Optional.of(warnings);
    }

    @Override
    public Optional<List<Warning>> findAppealedByContractor(Contractor contractor) throws SQLException {
        String sql = """
                SELECT w.*, c.*, u.*, cp.*
                FROM warning w 
                JOIN contractor c ON w.contractor_id = c.contractor_id 
                JOIN user u ON c.user_id = u.user_id 
                JOIN contractor_period cp ON c.contractor_period_id = cp.contractor_period_id
                WHERE w.contractor_id = ? AND w.state = 'APPEALED'
                """;

        List<Warning> warnings = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, contractor.getContractorId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    warnings.add(mapWarningFromResultSet(rs));
                }
            }
        }
        return Optional.of(warnings);
    }

    @Override
    public Optional<Warning> updateState(Warning warning) throws SQLException {
        String sql = "UPDATE warning SET state = ? WHERE warning_id = ?";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            con.setAutoCommit(false);
            Savepoint beforeWarningUpdate = con.setSavepoint();

            try {
                ps.setString(1, warning.getState().toString());
                ps.setLong(2, warning.getWarningId());

                if (ps.executeUpdate() > 0) {
                    con.commit();
                    return Optional.of(warning);
                }

                con.rollback(beforeWarningUpdate);
                return Optional.empty();

            } catch (SQLException e) {
                con.rollback(beforeWarningUpdate);
                throw e;
            }
        }
    }

    @Override
    public Optional<Warning> createLateWarning(Contractor contractor) throws SQLException {
        try (Connection con = getCon()) {
            con.setAutoCommit(false);
            Savepoint beforeLateWarning = con.setSavepoint();

            try {
                Warning warning = Warning.builder()
                        .contractor(contractor)
                        .dateIssue(LocalDateTime.now())
                        .reason(Warning.WarningReason.LATE)
                        .state(Warning.WarningState.ACTIVE)
                        .build();

                Optional<Warning> result = save(warning);
                if (result.isPresent()) {
                    con.commit();
                    return result;
                }

                con.rollback(beforeLateWarning);
                return Optional.empty();

            } catch (SQLException e) {
                con.rollback(beforeLateWarning);
                throw e;
            }
        }
    }

    @Override
    public Optional<List<Warning>> findWarningsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String sql = """
                SELECT w.*, c.*, u.*, cp.*
                FROM warning w 
                JOIN contractor c ON w.contractor_id = c.contractor_id 
                JOIN user u ON c.user_id = u.user_id 
                JOIN contractor_period cp ON c.contractor_period_id = cp.contractor_period_id
                WHERE w.date_issue BETWEEN ? AND ?
                """;

        List<Warning> warnings = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    warnings.add(mapWarningFromResultSet(rs));
                }
            }
        }
        return Optional.of(warnings);
    }

    @Override
    public Optional<List<Warning>> findFinalWarningsByContractor(Contractor contractor) throws SQLException {
        String sql = """
                SELECT w.*, c.*, u.*, cp.*
                FROM warning w 
                JOIN contractor c ON w.contractor_id = c.contractor_id 
                JOIN user u ON c.user_id = u.user_id 
                JOIN contractor_period cp ON c.contractor_period_id = cp.contractor_period_id
                WHERE w.contractor_id = ? AND w.state = 'FINAL'
                """;

        List<Warning> warnings = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, contractor.getContractorId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    warnings.add(mapWarningFromResultSet(rs));
                }
            }
        }
        return Optional.of(warnings);
    }

    @Override
    public Optional<Long> countActiveWarningsByContractor(Contractor contractor) throws SQLException {
        String sql = "SELECT COUNT(*) FROM warning WHERE contractor_id = ? AND state = 'ACTIVE'";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, contractor.getContractorId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong(1));
                }
            }
        }
        return Optional.of(0L);
    }


    @Override
    public Optional<List<Warning>> findWarningsByReason(Warning warning) throws SQLException {
        String reason = warning.getReason().name();
        String sql = """
                SELECT w.*, c.*, u.*, cp.*
                FROM warning w 
                JOIN contractor c ON w.contractor_id = c.contractor_id 
                JOIN user u ON c.user_id = u.user_id 
                JOIN contractor_period cp ON c.contractor_period_id = cp.contractor_period_id
                WHERE w.reason = ?
                """;

        List<Warning> warnings = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reason.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    warnings.add(mapWarningFromResultSet(rs));
                }
            }
        }
        return Optional.of(warnings);
    }

    @Override
    public Optional<Boolean> existsByContractorAndDateIssue(Contractor contractor, LocalDateTime dateIssue)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM warning WHERE contractor_id = ? AND DATE(date_issue) = DATE(?)";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, contractor.getContractorId());
            ps.setTimestamp(2, Timestamp.valueOf(dateIssue));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt(1) > 0);
                }
            }
        }
        return Optional.of(false);
    }

    private Warning mapWarningFromResultSet(ResultSet rs) throws SQLException {
        Contractor contractor = Contractor.builder()
                .contractorId(rs.getLong("contractor_id"))
                .status(Contractor.Status.valueOf(rs.getString("status")))
                .user(mapUserFromResultSet(rs))
                .contractPeriod(mapContractPeriodFromResultSet(rs))
                .build();

        return Warning.builder()
                .warningId(rs.getLong("warning_id"))
                .contractor(contractor)
                .dateIssue(rs.getTimestamp("date_issue").toLocalDateTime())
                .reason(Warning.WarningReason.valueOf(rs.getString("reason")))
                .state(Warning.WarningState.valueOf(rs.getString("state")))
                .build();
    }

    private ContractPeriod mapContractPeriodFromResultSet(ResultSet rs) throws SQLException {
        return ContractPeriod.builder()
                .contractPeriodId(rs.getLong("contractor_period_id"))
                .name(rs.getString("name"))
                .startDate(rs.getDate("start_date").toLocalDate())
                .endDate(rs.getDate("end_date").toLocalDate())
                .build();
    }

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        return User.builder()
                .userId(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .surname(rs.getString("surname"))
                .email(rs.getString("email"))
                .gender(rs.getString("gender"))
                .idNumber(rs.getString("id_number"))
                .role(User.Role.valueOf(rs.getString("role")))
                .race(rs.getString("race"))
                .location(rs.getString("location"))
                .age(rs.getInt("age"))
                .build();
    }
}
