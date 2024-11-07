package com.j148.backend.contract_period.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contract_period.model.ContractPeriod;

import java.sql.*;
import java.util.Optional;

/**
 * Martinez
 */
public class ContractPeriodRepoImpl extends DBConfig implements ContractPeriodRepo {

    @Override
    public Optional<ContractPeriod> saveContractPeriod(com.j148.backend.contract_period.model.ContractPeriod contractPeriod) throws SQLException {
        String query = "INSERT INTO contractor_period (name, start_date, end_date) VALUES (?, ?, ?)";

        try (Connection con = DBConfig.getCon()) {
            con.setAutoCommit(false);

            try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, contractPeriod.getName());
                stmt.setDate(2, Date.valueOf(contractPeriod.getStartDate()));
                stmt.setDate(3, Date.valueOf(contractPeriod.getEndDate()));
                Savepoint savepoint = con.setSavepoint("BeforeInsert");

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            contractPeriod.setContractPeriodId(generatedKeys.getLong(1));
                            con.commit();
                            return Optional.of(contractPeriod);
                        }
                    }
                } else {
                    con.rollback(savepoint);
                }

            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContractPeriod> findContractPeriodByName(String name) throws SQLException {
        String query = "SELECT contractor_period_id, name, start_date, end_date FROM contractor_period WHERE name = ?";

        try (Connection con = DBConfig.getCon(); PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    com.j148.backend.contract_period.model.ContractPeriod contractPeriod = new com.j148.backend.contract_period.model.ContractPeriod();
                    contractPeriod.setContractPeriodId(rs.getLong("contractor_period_id"));
                    contractPeriod.setName(rs.getString("name"));
                    contractPeriod.setStartDate(rs.getDate("start_date").toLocalDate());
                    contractPeriod.setEndDate(rs.getDate("end_date").toLocalDate());
                    return Optional.of(contractPeriod);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContractPeriod> updateContractPeriod(com.j148.backend.contract_period.model.ContractPeriod contractPeriod) throws SQLException {
        String query = "UPDATE contractor_period SET name = ?, start_date = ?, end_date = ? WHERE contractor_period_id = ?";

        try (Connection con = DBConfig.getCon()) {
            con.setAutoCommit(false);

            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, contractPeriod.getName());
                stmt.setDate(2, Date.valueOf(contractPeriod.getStartDate()));
                stmt.setDate(3, Date.valueOf(contractPeriod.getEndDate()));
                stmt.setLong(4, contractPeriod.getContractPeriodId());
                Savepoint savepoint = con.setSavepoint("BeforeUpdate");

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    con.commit();
                    return Optional.of(contractPeriod);
                } else {
                    con.rollback(savepoint);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContractPeriod> getCurrentContractPeriod() throws SQLException {
        String query = "SELECT * FROM contractor_period WHERE start_date < CURDATE() AND end_date > CURDATE()";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ContractPeriod contractPeriod = new ContractPeriod();
                    contractPeriod.setContractPeriodId(rs.getLong("contractor_period_id"));
                    contractPeriod.setName(rs.getString("name"));
                    contractPeriod.setStartDate(rs.getDate("start_date").toLocalDate());
                    contractPeriod.setEndDate(rs.getDate("end_date").toLocalDate());

                    return Optional.of(contractPeriod);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContractPeriod> getNextContractPeriod() throws SQLException {
        String query = "SELECT * FROM contractor_period WHERE start_date > CURDATE()";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ContractPeriod contractPeriod = ContractPeriod.builder().build();
                    contractPeriod.setContractPeriodId(rs.getLong("contractor_period_id"));
                    contractPeriod.setName(rs.getString("name"));
                    contractPeriod.setStartDate(rs.getDate("start_date").toLocalDate());
                    contractPeriod.setEndDate(rs.getDate("end_date").toLocalDate());

                    return Optional.of(contractPeriod);
                }
            }
        }
        return Optional.empty();
    }
    
    
}
