package com.j148.backend.user.service;

import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import com.j148.backend.user.repo.UserRepoImpl;

public class UserServiceImpl implements UserService{

    private UserRepo userRepo = new UserRepoImpl();

    @Override
    public User registerUser(User user) throws Exception {
        if(user != null){
            return this.userRepo.register(user).orElseThrow(() -> new Exception("Unable to insert user into the database"));
        }else{
            throw new IllegalArgumentException("User cannot be null");
        }
    }

    @Override
    public User updateUser(User user) throws Exception {
        if (user.getIdNumber() != null){
            return userRepo.updateUser(user).orElseThrow(() -> new Exception("Unable to update user."));
        }else {
            throw new IllegalArgumentException("ID number cannot be null.");
        }
    }

    @Override
    public User findUserByEmail(User user) throws Exception {
        if (user != null && user.getEmail() != null){
            return userRepo.retreiveUserFromEmail(user).orElseThrow(() -> new Exception("User with this email address was not found."));
        } else {
            throw  new IllegalArgumentException("Email cannot be null.");
        }
    }

    @Override
    public User promoteApplicant(User user) throws Exception {
        if (user != null){
            return userRepo.promoteApplicant(user).orElseThrow(() -> new Exception("User not found."));
        } else {
            throw  new IllegalArgumentException("User cannot be null.");
        }
    }

    @Override
    public User findUserById(User user) throws Exception {
        if (user != null && user.getUserId() != null){
            return userRepo.retreiveUserFromEmail(user).orElseThrow(() -> new Exception("User with this user id was not found."));
        } else {
            throw  new IllegalArgumentException("User id cannot be null.");
        }
    }
}
