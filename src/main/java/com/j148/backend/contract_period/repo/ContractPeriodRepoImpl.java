package com.j148.backend.contract_period.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contract_period.model.ContractPeriod;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**Martinez*/
@ApplicationScoped
public class ContractPeriodRepoImpl implements ContractPeriodRepo {

    @Inject
    private DBConfig DBConfig;
    @Override
    public Optional<ContractPeriod> saveContractPeriod(ContractPeriod contractPeriod) throws SQLException {
        String query = "INSERT INTO contractor_period (name, start_date, end_date) VALUES (?, ?, ?)";

        try (Connection con = DBConfig.getCon()) {

            try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, contractPeriod.getName());
                stmt.setDate(2, Date.valueOf(contractPeriod.getStartDate()));
                stmt.setDate(3, Date.valueOf(contractPeriod.getEndDate()));
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            contractPeriod.setContractPeriodId(generatedKeys.getLong(1));
                            return Optional.of(contractPeriod);
                        }
                    }
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
    public Optional<ContractPeriod> findById(ContractPeriod contractPeriod) throws SQLException {
        String query = "SELECT * FROM contractor_period WHERE  contractor_period_id = ?";

        try (Connection con = DBConfig.getCon();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, contractPeriod.getContractPeriodId());

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
    public double enrollmentAveragesForYear(int year) throws SQLException {
        String query = "SELECT COUNT(*) AS yearly_average FROM contractor_period WHERE YEAR(start_date) = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            // The int data type will work in this sql statement.
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int averageEnrollmentForYear = rs.getInt("yearly_average");
                    return averageEnrollmentForYear;
                }
            }

        }
        return 0;
    }

    @Override
    public double enrollmentAverageForPeriodOfYears(int startYear, int endYear) throws SQLException {
        String query = "SELECT COUNT(*) AS number_of_enrolled_contractors "
                + "FROM contractor_period "
                + "WHERE YEAR(start_date) BETWEEN ? AND ?";

        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, startYear);
            ps.setInt(2, endYear);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int totalEnrollments = rs.getInt("number_of_enrolled_contractors");
                    int numberOfYears = endYear - startYear + 1;

                    // Calculate the average
                    return (double) totalEnrollments / numberOfYears;

                }
            }

        }
        return 0;

    }

    @Override
    public Optional<ContractPeriod> updateContractPeriod(ContractPeriod contractPeriod) throws SQLException {
        String query = "UPDATE contractor_period SET name = ?, start_date = ?, end_date = ? WHERE contractor_period_id = ?";

        try (Connection con = DBConfig.getCon()) {

            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, contractPeriod.getName());
                stmt.setDate(2, Date.valueOf(contractPeriod.getStartDate()));
                stmt.setDate(3, Date.valueOf(contractPeriod.getEndDate()));
                stmt.setLong(4, contractPeriod.getContractPeriodId());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    return Optional.of(contractPeriod);
                }

            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContractPeriod> getCurrentContractPeriod() throws SQLException {
        String query = "SELECT * FROM contractor_period WHERE start_date <= CURDATE() AND end_date >= CURDATE()";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ContractPeriod contractPeriod = new ContractPeriod();
                    contractPeriod.setContractPeriodId(rs.getLong("contractor_period_id"));
                    contractPeriod.setName(rs.getString("name"));
                    contractPeriod.setStartDate(rs.getDate("start_date").toLocalDate());
                    contractPeriod.setEndDate(rs.getDate("end_date").toLocalDate());
                    System.out.println(contractPeriod);
                    return Optional.of(contractPeriod);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContractPeriod> getNextContractPeriod() throws SQLException {
        String query = "SELECT * FROM contractor_period WHERE start_date <= CURDATE()";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
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

    @Override
    public Optional<List<ContractPeriod>> getAllFutureContractPeriods() throws SQLException {
        List<ContractPeriod> contractPeriods = new ArrayList<>();
        String query = "SELECT * FROM contractor_period WHERE end_date > CURDATE()";

        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ContractPeriod contractPeriod = ContractPeriod.builder()
                            .contractPeriodId(rs.getLong("contractor_period_id"))
                            .name(rs.getString("name"))
                            .startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate())
                            .build();

                    contractPeriods.add(contractPeriod);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;  // Rethrow to propagate error
        }

        // If the list is empty, return an empty Optional
        return contractPeriods.isEmpty() ? Optional.empty() : Optional.of(contractPeriods);
    }



}