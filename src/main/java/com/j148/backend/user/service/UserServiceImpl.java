package com.j148.backend.user.service;

import com.j148.backend.notification.EmailSender;
import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import com.j148.backend.user.repo.UserRepoImpl;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    private UserRepo userRepo;

    @Inject
    private EmailSender emailSender;

    /**
     * This map is used to temporarily store the generated admin keys.
     */

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public User promoteUser(User user) throws SQLException, Exception { //promote Applicant to Contractor
        if (user != null && user.getEmail() != null) {
            user.setRole(User.Role.CONTRACTOR);
            return userRepo.promoteApplicant(user).orElseThrow(() -> new RuntimeException("Applicant was not promoted to Contractor"));
        } else {
            throw new IllegalArgumentException("The user is null");
        }
    }


    @Override
    public User LogIn(User user) throws SQLException, Exception {
        if (user != null) {
            if (user.getEmail() != null && user.getPassword() != null) {
                String email = user.getEmail();
                String password = user.getPassword();
                User foundUser = userRepo.retreiveUserFromEmail(user).orElseThrow(() -> new RuntimeException("User email not recognised"));
                if (email.equalsIgnoreCase(foundUser.getEmail()) && password.equals(foundUser.getPassword())) {
                    return foundUser;
                } else {
                    throw new RuntimeException("Invalid email or password");
                }
            } else {
                throw new IllegalArgumentException("Email or password is missing");
            }
        } else {
            throw new IllegalArgumentException("User is null");
        }
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public User registerUser(User user) throws Exception {

        //Checks the email format
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        return this.userRepo.register(user).orElseThrow(() -> new RuntimeException("Unable to insert user into the database."));
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public User updateUser(User user) throws Exception {
        if (user != null) {
            return userRepo.updateUser(user).orElseThrow(() -> new RuntimeException("Unable to update user."));
        } else {
            throw new IllegalArgumentException("ID number cannot be null.");
        }
    }

    @Override
    public User findUserByEmail(User user) throws Exception {
        if (user.getEmail() != null) {
            return userRepo.retreiveUserFromEmail(user).orElseThrow(() -> new RuntimeException("User with this email address was not found."));
        } else {
            throw new IllegalArgumentException("Email cannot be null.");
        }
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public User promoteApplicant(User user) throws Exception {
        if (user != null) {
            return userRepo.promoteApplicant(user).orElseThrow(() -> new RuntimeException("User not promoted to contractor."));
        } else {
            throw new IllegalArgumentException("User cannot be null.");
        }
    }

    @Override
    public User findUserById(User user) throws Exception {
        if (user != null && user.getUserId() != null) {
            return userRepo.retrieveUserFromUserID(user).orElseThrow(() -> new RuntimeException("User with this user id was not found."));
        } else {
            throw new IllegalArgumentException("User id cannot be null.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@gmail\\.com$";
        return email != null && email.matches(emailRegex);
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
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

            // Get the MonthDay of the input date
            MonthDay inputMonthDay = MonthDay.from(date);

            // Get today's MonthDay
            MonthDay todayMonthDay = MonthDay.from(LocalDate.now());

            // Compare MonthDay values (ignoring the year)
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
        emailSender.sendNotification(admin.getEmail(), "Birthdays today", messageBody);
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public User PromoteStaff(User user) throws Exception {
        if (user != null) {
            return userRepo.promoteStaff(user).orElseThrow(() -> new RuntimeException("User not promoted."));
        } else {
            throw new IllegalArgumentException("User cannot be null.");
        }
    }
}
