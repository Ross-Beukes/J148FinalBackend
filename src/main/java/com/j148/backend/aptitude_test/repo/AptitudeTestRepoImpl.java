
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.aptitude_test.repo;

/**
 * @author MIANTSUMI
 */

import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.config.DBConfig;
import com.j148.backend.user.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AptitudeTestRepoImpl extends DBConfig implements AptitudeRepo {

    @Override
    public Optional<AptitudeTest> create(AptitudeTest aptitudeTest) throws SQLException {
        String sql = "INSERT INTO aptitude_test (test_mark, test_date, user_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConfig.getCon();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            Savepoint beforeTestSave = conn.setSavepoint();

            try {
                stmt.setInt(1, aptitudeTest.getTestMark());
                stmt.setTimestamp(2, Timestamp.valueOf(aptitudeTest.getTestDate()));
                stmt.setLong(3, aptitudeTest.getUser().getUserId());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            aptitudeTest.setAptitudeTestId(rs.getLong(1));
                            conn.commit();
                            return Optional.of(aptitudeTest);

                        }
                    }
                }
            } catch (SQLException e) {
                conn.rollback(beforeTestSave);
                throw e;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<AptitudeTest> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM aptitude_test WHERE aptitude_test_id = ?";
        try (Connection conn = DBConfig.getCon();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAptitudeTest(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<AptitudeTest> findAll() throws SQLException {
        String sql = "SELECT * FROM aptitude_tests";
        List<AptitudeTest> aptitudeTests = new ArrayList<>();
        try (Connection conn = DBConfig.getCon();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                aptitudeTests.add(mapRowToAptitudeTest(rs));
            }
        }
        return aptitudeTests;
    }

    @Override
    public Optional<AptitudeTest> update(AptitudeTest aptitudeTest) throws SQLException {
        String query = "UPDATE aptitude_test SET test_mark = ?, test_date = ?, user_id = ? WHERE aptitude_test_id = ?";
        System.out.println(query);
        try (Connection conn = DBConfig.getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);
            stmt.setInt(1, aptitudeTest.getTestMark());
            stmt.setTimestamp(2, Timestamp.valueOf(aptitudeTest.getTestDate()));
            stmt.setLong(3, aptitudeTest.getUser().getUserId());
            stmt.setLong(4, aptitudeTest.getAptitudeTestId());
            Savepoint beforeTestSave = conn.setSavepoint();

            if (stmt.executeUpdate() > 0) {
                conn.commit();
                return Optional.of(aptitudeTest);
            } else {
                conn.rollback(beforeTestSave);
            }
            return Optional.empty();

        }
    }

    // Helper method to map a ResultSet row to an AptitudeTest object
    private AptitudeTest mapRowToAptitudeTest(ResultSet rs) throws SQLException {
        Long id = rs.getLong("aptitude_test_id");
        int testMark = rs.getInt("test_mark");
        LocalDateTime testDate = rs.getTimestamp("test_date").toLocalDateTime();
        Long userId = rs.getLong("user_id");

        // Assuming a method to get a User object by userId. This could be part of a UserRepository or other service.
        User user = new User(); // Or replace with user retrieval logic
        user.setUserId(userId);

        return AptitudeTest.builder()
                .aptitudeTestId(id)
                .testMark(testMark)
                .testDate(testDate)
                .user(user)
                .build();
    }

    @Override
    public Optional<AptitudeTest> retrieveAptitudeTestByUserId(User user) throws SQLException {
        String query = "SELECT * FROM aptitude_test WHERE user_id = ?";
        try(Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)){
            ps.setLong(1, user.getUserId());
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    AptitudeTest aptitudeTest = AptitudeTest.builder().aptitudeTestId(rs.getLong("aptitude_test_id"))
                            .testDate(rs.getTimestamp("test_date").toLocalDateTime()).testMark(rs.getInt("test_mark"))
                            .user(user).build();
                    return Optional.of(aptitudeTest);
                }
            }
        }
        return Optional.empty();
    }
}