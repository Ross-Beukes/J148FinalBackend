/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.notification;

/**
 *
 * @author arshr and mulalo
 */
import com.j148.backend.config.DBConfig;
import com.j148.backend.user.model.User;
import jakarta.ejb.Singleton;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TimeSheetReminder is scheduled service class for sending reminder emails to contractors
 * and notifying admins regarding outstanding timesheet submissions.
 *
 * This class uses scheduled methods annotated with @Scheduled to automatically run at specific times,
 * sending reminders 7 days, 3 days and 1 day before the timesheet deadlines, as well as  notifying admins
 * the day after the deadline
 * */
@Singleton
@ApplicationScoped
public class TimesheetReminder {
@Inject
private DBConfig DBConfig;


    /**
     * Calculates the number of days from the 15th of the previous month to the current date.
     *
     * @return the number of days between the 15th of the previous month and today.
     */
    public int calculateDaysTo15thOfPreviousMonth() {
        LocalDate today = LocalDate.now();
        LocalDate fifteenthOfLastMonth = today.minusMonths(1).withDayOfMonth(15);
        return (int) java.time.temporal.ChronoUnit.DAYS.between(fifteenthOfLastMonth, today);
    }

    /**
     * Retrieves a list of contractors who have not uploaded their timesheets by a calculated date.
     *
     * @param currentDate the date to base the calculation on.
     * @param days the number of days to subtract from the current date.
     * @return a list of users who have not uploaded their timesheets by the calculated date.
     * @throws SQLException if there is an error executing the query or retrieving data from the database.
     */
    public List<User> TimesheetOutstanding(LocalDate currentDate, int days) throws SQLException {
        List<User> users = new ArrayList<>();
        LocalDate calculatedDate = currentDate.minusDays(days);
        java.sql.Date sqlCalculatedDate = java.sql.Date.valueOf(calculatedDate);

        String query = "SELECT user.name AS user_name, user.surname, user.email "
                + "FROM contractor "
                + "JOIN user ON user.user_id = contractor.user_id "
                + "WHERE contractor.status = 'EXTERNAL' "
                + "AND NOT EXISTS ("
                + "    SELECT 1 "
                + "    FROM files "
                + "    WHERE files.user_id = user.user_id "
                + "    AND files.category = 'TIMESHEET' "
                + "    AND files.date_added >= ?"
                + ")";

        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, sqlCalculatedDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = User.builder()
                            .name(rs.getString("user_name"))
                            .surname(rs.getString("surname"))
                            .email(rs.getString("email"))
                            .build();
                    users.add(user);
                }
                return users;
            }
        }
    }

    /**
     * Retrieves a list of admins from the database to notify about outstanding timesheets.
     *
     * @return a list of admins.
     * @throws SQLException if there is an error executing the query or retrieving data from the database.
     */
    public List<User> getAdmins() throws SQLException {
        String query = "SELECT * FROM user WHERE user.role = 'ADMIN'";
        List<User> admins = new ArrayList<>();
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User admin = User.builder()
                            .name(rs.getString("name"))
                            .surname(rs.getString("surname"))
                            .email(rs.getString("email"))
                            .build();

                    admins.add(admin);
                }
            }
            return admins;
        }
    }
}
