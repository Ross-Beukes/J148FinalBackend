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
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * TimeSheetReminder is scheduled service class for sending reminder emails to contractors
 * and notifying admins regarding outstanding timsheet submissions.
 *
 * This class uses scheduled methods annotated with @Scheduled to automatically run at specific times,
 * sending reminders 7 days, 3 days and 1 day before the timesheet deadlines, as well as  notifying admins
 * the day after the deadline
 * */

@Singleton
public class TimesheetReminder extends DBConfig {
/**
 * Sends reminder email to users who have not updated their timesheets 7 days before the due date.
 *
 * This method is scheduled to run automatically on the first day of each month at 9:00 AM.
 * It retrieve a list of users who have not submitted their by a certain date and sends them
 * a reminder email to upload their timesheet before the 7th day of the month.
 *
 * @throws SQLException if there is an error retrieving user information from the database.
 * @throws MessagingException if there is an issue sending the email notification.
 * */
    @Schedule(hour = "11", minute = "25", dayOfMonth = "13", persistent = false)
    public void SevenDayReminder() {
        try {
            int daysToSubtract = calculateDaysTo15thOfPreviousMonth();
            LocalDate today = LocalDate.now();
            List<User> remindUsers = TimesheetOutstanding(today, daysToSubtract);
            StringBuilder greetings = new StringBuilder("Dear ");
            StringBuilder sb = new StringBuilder("You need to upload your timesheet before the end of the 7th of " + LocalDate.now().getMonth());


            for (User user : remindUsers) {
                StringBuilder msg = new StringBuilder();
                msg.append(user.getName()).append(" ");
                msg.append(user.getSurname()).append(" ");
                msg.append(sb);
                System.out.println("Hello world");

                EmailSender.sendNotification(user.getEmail(), msg.toString(), "Timesheet not Uploaded");
                wait(1500);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Sends a reminder email to users who have not uploaded their timesheets  7 days before the due date.
     *
     * This method is scheduled to run on the 3rd day of the month at 9:00 AM.
     * It checks for users who have  not uploaded their timesheets within a specified period,
     * reminding them to submit before the end of the 7th.
     *
     * @throws SQLException if there is an error retrieving user data from the database.
     * @throws MessagingException if an error occurs while sending email notification
     * */

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

    /**
    * Sends a reminder email to users who have not uploaded their timesheets 3 days before the due date.
    *
    * This method is scheduled to run on the 7th day of the month at 9:00 AM.
    * It retrieves users who have not submitted their timesheets within a specified period,
    * reminding them to upload by the end of the 7th.
    *
    * @throws SQLException if there is an error retrieving user data from the database.
    * @throws MessagingException if an error occurs while sending email notifications
    **/

    @Schedule(hour = "11", minute = "44", dayOfMonth = "13", persistent = false)
    public void OneDayReminder() {
        try {
            int daysToSubtract = calculateDaysTo15thOfPreviousMonth();
            LocalDate today = LocalDate.now();
            List<User> remindUsers = TimesheetOutstanding(today, daysToSubtract);
            StringBuilder sb = new StringBuilder("You need to upload your timesheet before the end of the 7th of " + LocalDate.now().getMonth());

            for (User user : remindUsers) {
                System.out.println(user);
                StringBuilder msg = new StringBuilder();
                msg.append(user.getName()).append(" ");
                msg.append(user.getSurname()).append(" ");
                msg.append(sb);
                System.out.println(user.getEmail());
                EmailSender.sendNotification(user.getEmail(), msg.toString(), "Timesheet not Uploaded");

            }
        } catch (SQLException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
/**
 * Sends an email notification to admin listing contractors who have not uploaded their timesheet by the due date.
 *
 * This method is scheduled to run on the 8th day of the month at 9:00 AM.
 * It retrieves a list users who have not uploaded their timesheet and compiles
 * a summary email sent to admins
 *
 * @throws SQLException if there is an error retrieving user or Admin data from the database.
 * @throws MessagingException if an error occurs while sending email notifications to Admins
 * */
    @Schedule(hour = "11", minute = "30", dayOfMonth = "13", persistent = false)
    public void AdminReminder() {
        try {
            int daysToSubtract = calculateDaysTo15thOfPreviousMonth();
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
                System.out.println("Hello world admin");
                EmailSender.sendNotification(admin.getEmail(), sb.toString(), "Timesheet not Uploaded");
                wait(1500);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Calculates the number of days from the 15th of the previous month to the current date.
     *
     * @return the number of days between the 15th of the previous month and today.
     */

    private int calculateDaysTo15thOfPreviousMonth() {
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
    /**
     * Retrieves a list of admins from the database to notify about outstanding timesheets.
     *
     * @return a list of admins.
     * @throws SQLException if there is an error executing the query or retrieving data from the database.
     */
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
