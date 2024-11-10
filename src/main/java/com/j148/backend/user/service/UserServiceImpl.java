package com.j148.backend.user.service;

import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import com.j148.backend.user.repo.UserRepoImpl;
import java.sql.SQLException;
import java.util.Random;

/**
 *
 * @author glenl
 */
public class UserServiceImpl implements UserService {

    private UserRepo userRepo = new UserRepoImpl();

    private Random random = new Random();

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
    public String generateInstructotToken() {
        StringBuilder token = new StringBuilder("I");
        char[] letters = new char[5];
        for (int i = 0; i < letters.length; i++) {
            letters[i] = (char) (65 + random.nextInt(122 - 65 + 1));
            token.append(letters[i]);
        }
        return token.toString();
    }

    @Override
    public User promoteUser(User user) throws SQLException, Exception {
        if (user != null && user.getIdNumber() != null) {
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

}
