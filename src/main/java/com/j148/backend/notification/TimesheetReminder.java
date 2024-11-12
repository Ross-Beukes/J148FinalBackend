/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.notification;

/**
 *
 * @author arshr
 */
import com.j148.backend.config.DBConfig;
import com.j148.backend.user.model.User;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.mail.MessagingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class TimesheetReminder extends DBConfig {

    @Schedule(hour = "9", minute = "0", dayOfMonth = "1", persistent = false)
    public void SevenDayReminder() throws SQLException, MessagingException {
        // Get today's date
        int daysToSubtract = 15;
        LocalDate today = LocalDate.now();
        List<User> remindUsers = TimesheetOutstanding(today, daysToSubtract);
        StringBuilder sb = new StringBuilder("You need to upload your timesheet before the end of the 7th of " + LocalDate.now().getMonth());

        for (User user : remindUsers) {
            StringBuilder msg = new StringBuilder();
            msg.append(user.getName()).append(" ");
            msg.append(user.getSurname()).append(" ");
            msg.append(sb);

            EmailSender.sendNotification(user.getEmail(), msg.toString(), "Timesheet not Uploaded");

        }
    }

    @Schedule(hour = "9", minute = "0", dayOfMonth = "3", persistent = false)
    public void ThreeDayReminder() throws SQLException, MessagingException {
        // Get today's date
        int daysToSubtract = 5;
        LocalDate today = LocalDate.now();
        List<User> remindUsers = TimesheetOutstanding(today, daysToSubtract);
        StringBuilder sb = new StringBuilder("You need to upload your timesheet before the end of the 7th of " + LocalDate.now().getMonth());

        for (User user : remindUsers) {
            StringBuilder msg = new StringBuilder();
            msg.append(user.getName()).append(" ");
            msg.append(user.getSurname()).append(" ");
            msg.append(sb);

            EmailSender.sendNotification(user.getEmail(), msg.toString(), "Timesheet not Uploaded");

        }
    }

    @Schedule(hour = "9", minute = "0", dayOfMonth = "7", persistent = false)
    public void OneDayReminder() throws SQLException, MessagingException {
        // Get today's date
        int daysToSubtract = 3;
        LocalDate today = LocalDate.now();
        List<User> remindUsers = TimesheetOutstanding(today, daysToSubtract);
        StringBuilder sb = new StringBuilder("You need to upload your timesheet before the end of the 7th of " + LocalDate.now().getMonth());

        for (User user : remindUsers) {
            StringBuilder msg = new StringBuilder();
            msg.append(user.getName()).append(" ");
            msg.append(user.getSurname()).append(" ");
            msg.append(sb);

            EmailSender.sendNotification(user.getEmail(), msg.toString(), "Timesheet not Uploaded");

        }
    }

    @Schedule(hour = "9", minute = "0", dayOfMonth = "8", persistent = false)
    public void AdminReminder() throws SQLException, MessagingException {
        // Get today's date
        int daysToSubtract = 0;
        LocalDate today = LocalDate.now();
        List<User> remindUsers = TimesheetOutstanding(today, daysToSubtract);
        List<User> emailAdmins = getAdmins();
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (User user : remindUsers) {
            count++;
            sb.append("Contractor " + count + "\n")
                    .append("name: ")
                    .append(user.getName())
                    .append(" ")
                    .append("surname: ")
                    .append(user.getSurname())
                    .append("\n")
                    .append("email: ")
                    .append(user.getEmail())
                    .append("\n");

        }

        for (User admin : emailAdmins) {
            EmailSender.sendNotification(admin.getEmail(), sb.toString(), "Timesheet not Uploaded");

        }

    }

    List<User> TimesheetOutstanding(LocalDate currentDate, int days) throws SQLException {
        List<User> users = new ArrayList<>();
        // Calculated date
        LocalDate calculatedDate = currentDate.minusDays(days);
        java.sql.Date sqlCalculatedDate = java.sql.Date.valueOf(calculatedDate);

        // Update the query to compare with the calculated date
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

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, sqlCalculatedDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = User.builder()
                            .name(rs.getString("user_name"))
                            .surname(rs.getString("surname"))
                            .email("email")
                            .build();
                    users.add(user);

                }
                return users;
            }

        }

    }

    List<User> getAdmins() throws SQLException {
        String query = "SELECT * FROM user WHERE user.role = ADMIN";
        List<User> admins = new ArrayList<>();
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                User admin = User.builder()
                        .name(rs.getString("name"))
                        .surname(rs.getString("surname"))
                        .email(rs.getString("email"))
                        .build();

                admins.add(admin);

            }
            return admins;
        }

    }

}
