/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
        String token = "A";
        char[] letters = new char[5];
        for (int i = 0; i < letters.length; i++) {
            letters[i] = (char) (65 + random.nextInt(122 - 65 + 1));
            token = token + letters[i];
        }
        return token;
    }

    @Override
    public String generateInstructotToken() {
        String token = "I";
        char[] letters = new char[5];
        for (int i = 0; i < letters.length; i++) {
            letters[i] = (char) (65 + random.nextInt(122 - 65 + 1));
            token = token + letters[i];
        }
        return token;
    }

    @Override
    public User promoteUser(User user) throws SQLException, Exception {
        if (user != null) {
            user.setRole(User.Role.CONTRACTOR);
            return userRepo.promoteApplicant(user).orElseThrow(() -> new Exception("Applicant was not promoted to Contractor"));
        } else {
            throw new IllegalArgumentException("The user is null"); 
        }
    }

}
