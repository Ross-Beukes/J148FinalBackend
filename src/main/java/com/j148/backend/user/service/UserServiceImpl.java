package com.j148.backend.user.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.service.ContractorService;
import com.j148.backend.contractor.service.ContractorServiceImpl;
import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import com.j148.backend.user.repo.UserRepoImpl;
import jakarta.ejb.Singleton;

import java.sql.SQLException;
import java.util.Random;

@Singleton
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo = new UserRepoImpl();
    private final ContractorService contractorService = new ContractorServiceImpl();
    /**
     *This map is used to temporarily store the generated admin keys.
     */
    private final Random random = new Random();

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
            User promotedUser = findUserByEmail(user);
            Contractor contractor = contractorService.promoteToContractor(promotedUser);
            return user;
        } else {
            throw new IllegalArgumentException("User cannot be null.");
        }
    }

    @Override
    public User findUserById(User user) throws Exception {
        if (user != null && user.getUserId() != null) {
            return userRepo.retrieveUserFromUserID(user).orElseThrow(() -> new Exception("User with this user id was not found."));
        } else {
            throw new IllegalArgumentException("User id cannot be null.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@gmail\\.com$";
        return email != null && email.matches(emailRegex);
    }

    @Override
    public User PromoteStaff(User user) throws Exception {
        if (user != null){
            return userRepo.promoteStaff(user).orElseThrow(() -> new Exception("User not promoted."));
        } else {
            throw new IllegalArgumentException("User cannot be null.");
        }
    }
}
