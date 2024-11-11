package com.j148.backend.user.service;

import com.j148.backend.user.model.User;


/**
 * Service interface for managing User entities in the system.
 * Provides methods for creating, retrieving, and updating Users.
 */
public interface UserService {

    /**
     * Registers a new User and saves the information in the database.
     *
     * @param user the User entity to be registered
     * @return Optional containing the registered User if successful, empty Optional otherwise
     * @throws Exception if an error occurs
     */
    User registerUser(User user) throws Exception;

    /**
     * Updates the details of an existing user.
     *
     * @param user the user entity containing the updated details
     * @return Optional containing the updated User if successful, empty Optional otherwise
     * @throws Exception if an error occurs
     */
    User updateUser(User user) throws Exception;

    /**
     * Retrieves a user by their email.
     *
     * @param user the user entity containing the email to search for
     * @return Optional containing the found User if it exists, empty Optional otherwise
     * @throws Exception if an error occurs
     */
    User findUserByEmail(User user) throws Exception;

    /**
     *Promotes Applicant to Contractor
     *
     * @param user the user entity to be promoted
     * @return Optional containing the user that is promoted, empty otherwise
     * @throws Exception if an error occurs
     */
    User promoteApplicant(User user) throws Exception;

    /**
     * Retrieves a user by their userid.
     *
     * @param user the user entity containing the userId to search for
     * @return Optional containing the found User if it exists, empty Optional otherwise
     * @throws Exception if an error occurs
     */
    User findUserById(User user) throws Exception;
}
