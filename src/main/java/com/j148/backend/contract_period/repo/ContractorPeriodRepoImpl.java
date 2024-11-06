/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contract_period.repo;

import com.j148.backend.config.DBConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author arshr
 */
public class ContractorPeriodRepoImpl extends DBConfig implements ContractorPeriodRepo {

    @Override
    public double enrollmentAveragesForYear(int year) throws SQLException {
        String query = "SELECT COUNT(*) AS yearly_average FROM contractor_period WHERE YEAR(start_date) = ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
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

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
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

}
