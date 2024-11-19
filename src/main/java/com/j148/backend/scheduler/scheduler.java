/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.scheduler;

import com.j148.backend.attendance.service.AttendanceService;
import com.j148.backend.attendance.service.AttendanceServiceImpl;
import com.j148.backend.user.EmailService;
import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import com.j148.backend.user.repo.UserRepoImpl;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author glenl
 */
@Singleton
public class scheduler {
    
    private AttendanceService attendanceService = new AttendanceServiceImpl();
    private UserRepo userRepo = new UserRepoImpl();
    
    @Schedule(dayOfWeek = "Mon-Fri", hour = "15", minute = "45", persistent = false)
    public void checkContractorsAttendance() {
        try {
            attendanceService.createAbsentContractors();
        } catch (Exception e) {

        }
    }
    
    @Schedule(dayOfWeek = "Mon-Sun", hour = "16", minute = "12", persistent = false)
    public void updateAge() throws Exception {
        List<User> allUsers;
        String messageBody = "";
        allUsers = userRepo.retrieveAllUsers();
        for (int i = 0; i < allUsers.size(); i++) {
            String birth = allUsers.get(i).getIdNumber().substring(0, 6);
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
                if (2024 - Integer.parseInt(year) != allUsers.get(i).getAge()) {
                    User user = allUsers.get(i);
                    user.setAge(LocalDate.now().getYear() - Integer.parseInt(year));
                    userRepo.updateAge(user).orElse(null);
                    messageBody = messageBody + user.getName() + " " + user.getSurname() + " : age: " + user.getAge() + " , " + user.getEmail() + "\n";
                }
            }
        }
        User admin = userRepo.getAdmin().orElse(null);
        if (!messageBody.equals("")) {
            EmailService.sendEmail(admin.getEmail(), "Birthdays today", messageBody);
        }
    }
}
