package com.j148.backend.contractPeriod.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contractPeriod.model.ContractPeriod;

import java.sql.*;
import java.util.Optional;
/**Martinez*/

public class ContractPeriodRepoImpl extends DBConfig implements ContractPeriodRepo {
    @Override
    public Optional<ContractPeriod> saveContractPeriod(ContractPeriod contractPeriod) throws SQLException {
        String query = "INSERT INTO contractor_period (name, start_date, end_date) VALUES (?, ?, ?)";

        try (Connection con = getCon()) {
            con.setAutoCommit(false);

            try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, contractPeriod.getName());
                stmt.setDate(2, Date.valueOf(contractPeriod.getStartDate()));
                stmt.setDate(3, Date.valueOf(contractPeriod.getEndDate()));
                Savepoint savepoint = con.setSavepoint();

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            contractPeriod.setContractPeriodId(generatedKeys.getLong(1));
                            con.commit();
                            return Optional.of(contractPeriod);
                        }
                    }
                }else {
                    con.rollback(savepoint);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContractPeriod> findContractPeriodByName(String name) throws SQLException {
        String query = "SELECT contractor_period_id, name, start_date, end_date FROM contractor_period WHERE name = ?";

        try (Connection con = DBConfig.getCon();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                            ContractPeriod.builder()
                                    .contractPeriodId(rs.getLong("contractor_period_id"))
                                    .name(rs.getString("name"))
                                    .startDate(rs.getDate("start_date").toLocalDate())
                                    .endDate(rs.getDate("end_date").toLocalDate())
                            .build()
                    );
                }
            }
        }
        return Optional.empty();
    }
    @Override
    public Optional<ContractPeriod> findContractPeriodById(long contractPeriodId) throws SQLException {
        String query = "SELECT contractor_period_id, name, start_date, end_date FROM contractor_period WHERE  contractor_period_id = ?";

        try (Connection con = DBConfig.getCon();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, contractPeriodId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                            ContractPeriod.builder()
                                    .contractPeriodId(rs.getLong("contractor_period_id"))
                                    .name(rs.getString("name"))
                                    .startDate(rs.getDate("start_date").toLocalDate())
                                    .endDate(rs.getDate("end_date").toLocalDate())
                                    .build()
                    );
                }
            }
        }
        return Optional.empty();
    }
    @Override
    public Optional<ContractPeriod> updateContractPeriod(ContractPeriod contractPeriod) throws SQLException {
        String query = "UPDATE contractor_period SET name = ?, start_date = ?, end_date = ? WHERE contractor_period_id = ?";

        try (Connection con = DBConfig.getCon()) {
            con.setAutoCommit(false);

            Savepoint savepoint = con.setSavepoint();
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, contractPeriod.getName());
                stmt.setDate(2, Date.valueOf(contractPeriod.getStartDate()));
                stmt.setDate(3, Date.valueOf(contractPeriod.getEndDate()));
                stmt.setLong(4, contractPeriod.getContractPeriodId());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    con.commit();
                    return Optional.of(contractPeriod);
                }else {
                    con.rollback(savepoint);
                }
            }
        }
        return Optional.empty();
    }
}
