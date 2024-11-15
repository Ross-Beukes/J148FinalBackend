package com.j148.backend.user.service;

import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import com.j148.backend.user.repo.UserRepoImpl;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class UserServiceImpl implements UserService {

    private UserRepo userRepo = new UserRepoImpl();
    /**
     *This map is used to temporarily store the generated admin keys.
     */
    private Random random = new Random();

    @Override
    public String generateVerificationToken(){
        StringBuilder token = new StringBuilder("V");
        char[] letters = new char[5];
        for (int i = 0; i < letters.length; i++) {
            letters[i] = (char) (65 + random.nextInt(122 - 65 + 1));
            token.append(letters[i]);
        }
        return token.toString();
    }

    @Override
    public String generateAdminToken() {
        StringBuilder token = new StringBuilder("A");
        char[] letters = new char[5];
        for (int i = 0; i < letters.length; i++) {
            letters[i] = (char) (65 + random.nextInt(122 - 65 + 1));
            token.append(letters[i]);
        }
        return token.toString();
    }

    @Override
    public String generateInstructorToken() {
        StringBuilder token = new StringBuilder("I");
        char[] letters = new char[5];
        for (int i = 0; i < letters.length; i++) {
            letters[i] = (char) (65 + random.nextInt(122 - 65 + 1));
            token.append(letters[i]);
        }
        return token.toString();
    }

    @Override
    public User promoteUser(User user) throws SQLException, Exception { //promote Applicant to Contractor
        if (user != null && user.getEmail() != null) {
            user.setRole(User.Role.CONTRACTOR);
            return userRepo.promoteApplicant(user).orElseThrow(() -> new Exception("Applicant was not promoted to Contractor"));
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
                User foundUser = userRepo.retreiveUserFromEmail(user).orElseThrow(() -> new Exception("User email not recognised"));
                if (email.equalsIgnoreCase(foundUser.getEmail()) && password.equals(foundUser.getPassword())) {
                    return foundUser;
                } else {
                    throw new Exception("Invalid email or password");
                }
            } else {
                throw new IllegalArgumentException("Email or password is missing");
            }
        } else {
            throw new IllegalArgumentException("User is null");
        }
    }

    @Override
    public User registerUser(User user) throws Exception {

        //Checks the email format
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        return this.userRepo.register(user).orElseThrow(() -> new Exception("Unable to insert user into the database."));
    }

    @Override
    public User updateUser(User user) throws Exception {
        if (user != null){
            return userRepo.updateUser(user).orElseThrow(() -> new Exception("Unable to update user."));
        } else {
            throw new IllegalArgumentException("ID number cannot be null.");
        }
    }

    @Override
    public User findUserByEmail(User user) throws Exception {
        if (user.getEmail() != null) {
            return userRepo.retreiveUserFromEmail(user).orElseThrow(() -> new Exception("User with this email address was not found."));
        } else {
            throw new IllegalArgumentException("Email cannot be null.");
        }
    }

    @Override
    public User promoteApplicant(User user) throws Exception {
        if (user != null){
            return userRepo.promoteApplicant(user).orElseThrow(() -> new Exception("User not promoted to contractor."));
        } else {
            throw new IllegalArgumentException("User cannot be null.");
        }
    }

    @Override
    public User findUserById(User user) throws Exception {
        if (user != null && user.getUserId() != null) {
            return userRepo.retreiveUserFromUserID(user).orElseThrow(() -> new Exception("User with this user id was not found."));
        } else {
            throw new IllegalArgumentException("User id cannot be null.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@gmail\\.com$";
        return email != null && email.matches(emailRegex);
    }

    @Schedule(dayOfWeek = "Mon-Sun", hour = "13", minute = "48", persistent = false)
    public void updateAge() throws Exception {
        List<User> allUsers;
        allUsers = userRepo.retreiveAllUsers();
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
                   System.out.println(user);
                   userRepo.updateAge(user).orElse(null);

                }
            } else {
                System.out.println("The date is not the same day and month as today.");
            }
        }
    }
}
