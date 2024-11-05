/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.aptitude_test.repo;

/**
 *
 * @author MIANTSUMI
 */

import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.user.model.User;
import com.j148.backend.config.DBConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AptitudeTestRepoImpl implements AptitudeRepo {

    @Override
    public AptitudeTest create (AptitudeTest aptitudeTest) throws SQLException {
        String sql = "INSERT INTO aptitude_tests (test_mark, test_date, user_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConfig.getCon();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, aptitudeTest.getTestMark());
            stmt.setTimestamp(2, Timestamp.valueOf(aptitudeTest.getTestDate()));
            stmt.setLong(3, aptitudeTest.getUser().getUserId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) { 
                    if (rs.next()) {
                        aptitudeTest.setAptitudeTestId(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aptitudeTest;
    }

    @Override
    public Optional<AptitudeTest> findById(Long id) throws SQLException  {
        String sql = "SELECT * FROM aptitude_tests WHERE aptitude_test_id = ?";
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aptitudeTests;
    }

    @Override
    public AptitudeTest update(AptitudeTest aptitudeTest) throws SQLException {
        String sql = "UPDATE aptitude_tests SET test_mark = ?, test_date = ?, user_id = ? WHERE aptitude_test_id = ?";
        try (Connection conn = DBConfig.getCon();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, aptitudeTest.getTestMark());
            stmt.setTimestamp(2, Timestamp.valueOf(aptitudeTest.getTestDate()));
            stmt.setLong(3, aptitudeTest.getUser().getUserId());
            stmt.setLong(4, aptitudeTest.getAptitudeTestId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aptitudeTest;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM aptitude_tests WHERE aptitude_test_id = ?";
        try (Connection conn = DBConfig.getCon();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
}