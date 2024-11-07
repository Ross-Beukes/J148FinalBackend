package com.j148.backend.contractor.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.user.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContractorRepoImpl extends DBConfig implements ContractorRepo {

    @Override
    public Optional<Contractor> save(Contractor contractor) throws SQLException {
        String sql = "INSERT INTO contractor (contractor_id, status, user_id) VALUES (?, ?, ?)";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            con.setAutoCommit(false);
            Savepoint beforeSave = con.setSavepoint();

            try {
                ps.setLong(1, contractor.getContractorId());
                ps.setString(2, contractor.getStatus().toString());
                ps.setLong(3, contractor.getUser().getUserId());

                if (ps.executeUpdate() > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            contractor.setContractorId(generatedKeys.getLong(1));  // Set generated ID
                            con.commit();
                            return Optional.of(contractor);
                        }
                    }
                }
                con.rollback(beforeSave);
                return Optional.empty();

            } catch (SQLException e) {
                con.rollback(beforeSave);
                throw e;
            }
        }
    }

    @Override
    public Optional<Contractor> findById(Long contractorId) throws SQLException {
        String sql = "SELECT contractor_id, status, user_id FROM contractor WHERE contractor_id = ?";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, contractorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getLong("user_id"));

                    Contractor contractor = Contractor.builder()
                            .contractorId(rs.getLong("contractor_id"))
                            .status(Contractor.Status.valueOf(rs.getString("status")))
                            .user(user)
                            .build();

                    return Optional.of(contractor);
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Contractor> update(Contractor contractor) throws SQLException {
        String sql = "UPDATE contractor SET status = ?, user_id = ? WHERE contractor_id = ?";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            con.setAutoCommit(false);
            Savepoint beforeUpdate = con.setSavepoint();

            try {
                ps.setString(1, contractor.getStatus().toString());
                ps.setLong(2, contractor.getUser().getUserId());
                ps.setLong(3, contractor.getContractorId());

                if (ps.executeUpdate() > 0) {
                    con.commit();
                    return Optional.of(contractor);
                }
                con.rollback(beforeUpdate);
                return Optional.empty();

            } catch (SQLException e) {
                con.rollback(beforeUpdate);
                throw e;
            }
        }
    }

    @Override
    public List<Contractor> findAll() throws SQLException {
        String sql = "SELECT contractor_id, status, user_id FROM contractor";
        List<Contractor> contractors = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));

                Contractor contractor = Contractor.builder()
                        .contractorId(rs.getLong("contractor_id"))
                        .status(Contractor.Status.valueOf(rs.getString("status")))
                        .user(user)
                        .build();

                contractors.add(contractor);
            }
        }
        return contractors;
    }

    @Override
    public List<Contractor> findCurrentContractor(ContractPeriod contractPeriod) throws SQLException {
        String query = "SELECT * FROM contractor WHERE contract_period_id = ?";
        List<Contractor> currentContractors = new ArrayList<>();
        
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            User user = new User();
            ContractPeriod currentContractPeriod = new ContractPeriod();
            
            try (ResultSet rs = ps.executeQuery()) {
                user.setUserId(rs.getLong("user_id"));
                currentContractPeriod.setContractPeriodId(rs.getLong("contractor_period_id"));
                
                Contractor contractor = Contractor.builder()
                        .contractorId(rs.getLong("contractor_id"))
                        .status(Contractor.Status.valueOf(rs.getString("status")))
                        .user(user)
                        .contractPeriod(currentContractPeriod)
                        .build();
                currentContractors.add(contractor);
            }
            
        }
        return currentContractors;
    }
}



