package com.j148.backend.aptitude_test.service;

import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.aptitude_test.repo.AptitudeRepo;
import com.j148.backend.aptitude_test.repo.AptitudeTestRepoImpl;
import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import com.j148.backend.user.repo.UserRepoImpl;

import java.time.LocalDateTime;

public class AptitudeTestServiceImpl implements AptitudeTestService {

    private final AptitudeRepo aptitudeRepo = new AptitudeTestRepoImpl();
    private final UserRepo userRepo = new UserRepoImpl();

    @Override
    public AptitudeTest scheduleTest(AptitudeTest aptitudeTest, User user) throws Exception {

        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (aptitudeTest == null) {
            throw new IllegalArgumentException("Aptitude test is null");
        }

        if (aptitudeTest.getAptitudeTestId() == null || aptitudeTest.getTestDate() == null) {


            throw new IllegalArgumentException("Aptitude test date or id is null");
        }


        if (userRepo.retrieveUserFromUserID(user).isEmpty()){
            throw new IllegalArgumentException("User not found");
        }

        if (aptitudeTest.getTestDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Test date must be in the future");
        }

        aptitudeTest.setUser(user);

        return aptitudeRepo.create(aptitudeTest).orElseThrow(() -> new Exception("failed to schedule aptitude test"));
    }

    @Override
    public AptitudeTest rescheduleTest(AptitudeTest aptitudeTest, User user) throws Exception {

        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (aptitudeTest.getAptitudeTestId() == null || aptitudeTest.getTestDate() == null) {
            throw new IllegalArgumentException("Aptitude test date or id is null");
        }

        if (userRepo.retrieveUserFromUserID(user).isEmpty()){

            throw new IllegalArgumentException("User not found");
        }

        if (aptitudeRepo.findById(aptitudeTest.getAptitudeTestId()).isEmpty()) {
            throw new IllegalArgumentException("Aptitude test not found");
        }

        if (aptitudeTest.getTestDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Test date must be in the future");
        }

        aptitudeTest.setUser(user);

        return aptitudeRepo.update(aptitudeTest).orElseThrow(() -> new Exception("failed to reschedule aptitude test"));
    }

    @Override
    public AptitudeTest retrieveAptitudeTestByUserId(User user) throws Exception {
        if (user != null) {
            return aptitudeRepo.retrieveAptitudeTestByUserId(user).orElseThrow(()
                    -> new Exception("Could not retrieve aptitude test by user ID"));
        } else {
            throw new NullPointerException("User cannot be null when retrieving aptitude test by user ID");
        }
    }
}
