/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.scheduler;

import com.j148.backend.attendance.service.AttendanceService;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.service.ContractorService;
import com.j148.backend.leave_request.model.LeaveRequest;
import com.j148.backend.leave_request.service.LeaveRequestService;
import com.j148.backend.notification.EmailSender;
import com.j148.backend.notification.TimesheetReminder;
import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author glenl
 */
@Singleton
public class Scheduler {

    @Inject
    private AttendanceService attendanceService;

    @Inject
    private UserRepo userRepo;

    @Inject
    private TimesheetReminder timesheetReminder;

    @Inject
    private EmailSender emailSender;

    @Inject
    private LeaveRequestService leaveRequestService;

    @Inject
    private ContractorService contractorService;

    @Schedule(dayOfWeek = "Mon-Fri", hour = "13", minute = "28", persistent = false)
    public void checkContractorsAttendance() {
        try {
            attendanceService.createAbsentContractors();
        } catch (Exception e) {

        }
    }

    @Schedule(dayOfWeek = "Mon-Sun", hour = "16", minute = "12", persistent = false)
    public void updateAge() throws Exception {
        List<User> allUsers;
        StringBuilder messageBody = new StringBuilder();
        allUsers = userRepo.retrieveAllUsers();
        for (User allUser : allUsers) {
            String birth = allUser.getIdNumber().substring(0, 6);
            String year = birth.substring(0, 2);
            int yearBorn = 2000 + Integer.parseInt(year);
            if (yearBorn < LocalDateTime.now().getYear()) {
                year = "20" + year;
            } else {
                year = "19" + year;
            }
            String month = birth.substring(2, 4);
            String day = birth.substring(4, 6);
            String birthday = year + "-" + month + "-" + day;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(birthday, formatter);

            MonthDay inputMonthDay = MonthDay.from(date);

            MonthDay todayMonthDay = MonthDay.from(LocalDate.now());

            if (inputMonthDay.equals(todayMonthDay)) {
                if (2024 - Integer.parseInt(year) != allUser.getAge()) {
                    allUser.setAge(LocalDate.now().getYear() - Integer.parseInt(year));
                    userRepo.updateAge(allUser).orElse(null);
                    messageBody.append(allUser.getName()).append(" ").append(allUser.getSurname()).append(" : age: ").append(allUser.getAge()).append(" , ").append(allUser.getEmail()).append("\n");
                }
            }
        }
        User admin = userRepo.getAdmin().orElse(null);
        if (!messageBody.toString().isEmpty()) {

            emailSender.sendNotification(admin.getEmail(), "Birthdays today", messageBody.toString());
        }
    }

    /**
     * Sends reminder email to users who have not updated their timesheets 7
     * days before the due date.
     *
     * This method is scheduled to run automatically on the first day of each
     * month at 9:00 AM. It retrieves a list of users who have not submitted
     * their by a certain date and sends them a reminder email to upload their
     * timesheet before the 7th day of the month.
     *
     * @throws SQLException if there is an error retrieving user information
     * from the database.
     * @throws MessagingException if there is an issue sending the email
     * notification.
     *
     */
    @Schedule(hour = "9", minute = "00", dayOfMonth = "1", persistent = false)
    public void SevenDayReminder() {
        try {
            int daysToSubtract = timesheetReminder.calculateDaysTo15thOfPreviousMonth();
            LocalDate today = LocalDate.now();
            List<User> remindUsers = timesheetReminder.TimesheetOutstanding(today, daysToSubtract);
            StringBuilder greetings = new StringBuilder("Dear ");
            StringBuilder sb = new StringBuilder();
            sb.append("We hope you are doing well!").append("\n")
                    .append("We have noticed that you have not uploaded your timesheet for last month. ")
                    .append("Please do so before the 8th of " + LocalDate.now().getMonth() + ".").append("\n\n")
                    .append("Kind regards").append("\n")
                    .append("The HR Department");

            for (User user : remindUsers) {
                StringBuilder msg = new StringBuilder();
                msg.append(greetings).append(user.getName()).append(" ").append(user.getSurname()).append("\n");
                msg.append(sb);

                emailSender.sendNotification(user.getEmail(), msg.toString(), "Timesheet not Uploaded");

            }
        } catch (SQLException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sends a reminder email to users who have not uploaded their timesheets 7
     * days before the due date.
     *
     * This method is scheduled to run on the 3rd day of the month at 9:00 AM.
     * It checks for users who have not uploaded their timesheets within a
     * specified period, reminding them to submit before the end of the 7th.
     *
     * @throws SQLException if there is an error retrieving user data from the
     * database.
     * @throws MessagingException if an error occurs while sending email
     * notification
     *
     */
    @Schedule(hour = "9", minute = "0", dayOfMonth = "3", persistent = false)
    public void ThreeDayReminder() {
        try {
            int daysToSubtract = timesheetReminder.calculateDaysTo15thOfPreviousMonth();
            LocalDate today = LocalDate.now();
            List<User> remindUsers = timesheetReminder.TimesheetOutstanding(today, daysToSubtract);
            StringBuilder greetings = new StringBuilder("Dear ");
            StringBuilder sb = new StringBuilder();
            sb.append("We hope you are doing well!").append("\n")
                    .append("We have noticed that you have not uploaded your timesheet for last month.").append("\n")
                    .append("Please do so before the 8th of " + LocalDate.now().getMonth() + ".").append("\n\n")
                    .append("Kind regards").append("\n")
                    .append("The HR Department");

            for (User user : remindUsers) {
                StringBuilder msg = new StringBuilder();
                msg.append(greetings).append(user.getName()).append(" ").append(user.getSurname()).append("\n");
                msg.append(sb);

                emailSender.sendNotification(user.getEmail(), msg.toString(), "Timesheet not Uploaded");

            }
        } catch (SQLException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sends a reminder email to users who have not uploaded their timesheets 3
     * days before the due date.
     *
     * This method is scheduled to run on the 7th day of the month at 9:00 AM.
     * It retrieves users who have not submitted their timesheets within a
     * specified period, reminding them to upload by the end of the 7th.
     *
     * @throws SQLException if there is an error retrieving user data from the
     * database.
     * @throws MessagingException if an error occurs while sending email
     * notifications
     *
     */
    @Schedule(hour = "9", minute = "0", dayOfMonth = "7", persistent = false)
    public void OneDayReminder() {
        try {
            int daysToSubtract = timesheetReminder.calculateDaysTo15thOfPreviousMonth();
            LocalDate today = LocalDate.now();
            List<User> remindUsers = timesheetReminder.TimesheetOutstanding(today, daysToSubtract);
            StringBuilder greetings = new StringBuilder("Dear ");
            StringBuilder sb = new StringBuilder();
            sb.append("We hope you are doing well!").append("\n")
                    .append("We have noticed that you have not uploaded your timesheet for last month.").append("\n")
                    .append("Please do so before the 8th of " + LocalDate.now().getMonth() + ".").append("\n\n")
                    .append("Kind regards").append("\n")
                    .append("The HR Department");

            for (User user : remindUsers) {
                StringBuilder msg = new StringBuilder();
                msg.append(greetings).append(user.getName()).append(" ").append(user.getSurname()).append("\n");
                msg.append(sb);

                emailSender.sendNotification(user.getEmail(), msg.toString(), "Timesheet not Uploaded");

            }
        } catch (SQLException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sends an email notification to admin listing contractors who have not
     * uploaded their timesheet by the due date.
     *
     * This method is scheduled to run on the 8th day of the month at 9:00 AM.
     * It retrieves a list users who have not uploaded their timesheet and
     * compiles a summary email sent to admins
     *
     * @throws SQLException if there is an error retrieving user or Admin data
     * from the database.
     * @throws MessagingException if an error occurs while sending email
     * notifications to Admins
     *
     */
    @Schedule(hour = "9", minute = "0", dayOfMonth = "8", persistent = false)
    public void AdminReminder() {
        try {
            int daysToSubtract = timesheetReminder.calculateDaysTo15thOfPreviousMonth();
            LocalDate today = LocalDate.now();
            List<User> remindUsers = timesheetReminder.TimesheetOutstanding(today, daysToSubtract);
            List<User> emailAdmins = timesheetReminder.getAdmins();

            StringBuilder sb = new StringBuilder();
            int count = 0;

            for (User user : remindUsers) {
                count++;
                sb.append("Contractor " + count + "\n")
                        .append("Name: ")
                        .append(user.getName()).append("\n")
                        .append("Surname: ")
                        .append(user.getSurname()).append("\n")
                        .append("Email: ")
                        .append(user.getEmail()).append("\n\n");

            }

            for (User admin : emailAdmins) {
                StringBuilder email = new StringBuilder();
                email.append("Dear ").append(admin.getName()).append(" ").append(admin.getSurname()).append("\n")
                        .append("Please see the list of contractors who have not uploaded timesheets.").append("\n\n")
                        .append(sb).append("\n")
                        .append("System generated response");
                emailSender.sendNotification(admin.getEmail(), email.toString(), "Timesheet not Uploaded");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(TimesheetReminder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Schedule(hour = "8", minute = "53", persistent = false)
    public void setContractorToOnLeave() {
        try {
            List<LeaveRequest> leaveRequestList = getApprovedLeaveRequests();
            LocalDate today = LocalDate.now();

            for (LeaveRequest leaveRequest : leaveRequestList) {
                if (leaveRequest.getStartDate().equals(today)) {
                    Contractor contractor = leaveRequest.getContractor();
                    contractor.setStatus(Contractor.Status.ON_LEAVE);
                    contractor = contractorService.changeContractorStatus(contractor);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(LeaveRequest.class.getName()).log(Level.SEVERE, "Error in SetContractorToOnLeave", e);
        }
    }

    @Schedule(hour = "8", minute = "55", persistent = false)
    public void setContractorToActive() {
        try {
            List<LeaveRequest> leaveRequestList = getApprovedLeaveRequests();
            LocalDate today = LocalDate.now();

            for (LeaveRequest leaveRequest : leaveRequestList) {
                if (leaveRequest.getEndDate().equals(today)) {
                    Contractor contractor = leaveRequest.getContractor();
                    contractor.setStatus(Contractor.Status.ACTIVE);
                    contractor = contractorService.changeContractorStatus(contractor);
                }
            }
            
        } catch (Exception e) {
            Logger.getLogger(LeaveRequest.class.getName()).log(Level.SEVERE, "Error in SetContractorToActive", e);
        }
    }

    // Helper Method to Retrieve Approved Leave Requests
    private List<LeaveRequest> getApprovedLeaveRequests() {
        try {
            AbstractMap<Long, LeaveRequest> leaveRequests = leaveRequestService.retrieveAllLeaveRequestsByDecision("APPROVED");
            return new ArrayList<>(leaveRequests.values());
        } catch (Exception e) {
            Logger.getLogger(LeaveRequest.class.getName()).log(Level.SEVERE, "Error in SetContractorToActive", e);
        }
        return new ArrayList<>();
    }

}
