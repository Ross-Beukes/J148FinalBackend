package com.j148.backend.aptitude_test.service;

import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.aptitude_test.repo.AptitudeRepo;
import com.j148.backend.aptitude_test.repo.AptitudeTestRepoImpl;
import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import com.j148.backend.user.repo.UserRepoImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.time.LocalDateTime;
@ApplicationScoped
public class AptitudeTestServiceImpl implements AptitudeTestService {

    @Inject
    private AptitudeRepo aptitudeRepo;
    @Inject
    private UserRepo userRepo ;

    @Transactional(dontRollbackOn = { IllegalArgumentException.class, IllegalStateException.class},rollbackOn = {SQLException.class})
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

        return aptitudeRepo.create(aptitudeTest).orElseThrow(() -> new RuntimeException("failed to schedule aptitude test"));
    }

    @Transactional(dontRollbackOn = { IllegalArgumentException.class, IllegalStateException.class},rollbackOn = {SQLException.class})
    @Override
    public AptitudeTest rescheduleTest(AptitudeTest aptitudeTest, User user) throws Exception {

        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (aptitudeTest.getTestDate() == null) {
            throw new IllegalArgumentException("Aptitude test date or id is null");
        }

        if (userRepo.retrieveUserFromUserID(user).isEmpty()){

            throw new IllegalArgumentException("User not found");
        }

        if (aptitudeRepo.retrieveAptitudeTestByUserId(user).isEmpty()) {
            throw new IllegalArgumentException("Aptitude test not found");
        }

        if (aptitudeTest.getTestDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Test date must be in the future");
        }

        aptitudeTest.setUser(user);

        return aptitudeRepo.update(aptitudeTest).orElseThrow(() -> new RuntimeException("failed to reschedule aptitude test"));
    }

    @Override
    public AptitudeTest retrieveAptitudeTestByUserId(User user) throws Exception {
        if (user != null) {
            return aptitudeRepo.retrieveAptitudeTestByUserId(user).orElseThrow(()
                    -> new RuntimeException("Could not retrieve aptitude test by user ID"));
        } else {
            throw new NullPointerException("User cannot be null when retrieving aptitude test by user ID");
        }
    }
}
