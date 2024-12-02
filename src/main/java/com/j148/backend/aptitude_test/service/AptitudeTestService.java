package com.j148.backend.aptitude_test.service;

import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.user.model.User;
import java.util.List;

/**
 * Service interface for managing aptitude test operations in the HRMS system.
 */
public interface AptitudeTestService {

    /**
     * Schedules a new aptitude test for a user.
     *
     * @param aptitudeTest the aptitude test details to be scheduled
     * @param user the user for whom the test is being scheduled
     * @return the scheduled AptitudeTest with updated information
     * @throws IllegalArgumentException if:
     *         - user is null or has null ID
     *         - aptitudeTest is null
     *         - aptitudeTest has null ID or test date
     *         - user does not exist in the system
     *         - test date is in the past
     * @throws Exception if the scheduling operation fails
     */
    AptitudeTest scheduleTest(AptitudeTest aptitudeTest, User user) throws Exception;

    /**
     * Reschedules an existing aptitude test for a user.
     *
     * @param aptitudeTest the aptitude test with updated schedule details
     * @param user the user whose test is being rescheduled
     * @return the rescheduled AptitudeTest with updated information
     * @throws IllegalArgumentException if:
     *         - user is null or has null ID
     *         - aptitudeTest has null ID or test date
     *         - user does not exist in the system
     *         - aptitude test does not exist in the system
     *         - new test date is in the past
     * @throws Exception if the rescheduling operation fails
     */
    AptitudeTest rescheduleTest(AptitudeTest aptitudeTest, User user) throws Exception;
    
    /**
     * Retrieves a specific users aptitude test by their user ID.
     * @param user
     * @return
     * @throws Exception 
     */
    AptitudeTest retrieveAptitudeTestByUserId(User user) throws Exception;
    
    List<AptitudeTest> getAllWrittenTests() throws Exception;
    
    AptitudeTest update(AptitudeTest aptitudeTest) throws Exception;
}
