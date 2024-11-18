package com.j148.backend.user.service;

import com.j148.backend.user.model.User;

import java.sql.SQLException;


/**
 * Service interface for managing User entities in the system.
 * Provides methods for creating, retrieving, and updating Users.
 */
public interface UserService {

    /**

     * Generates a unique verification token.
     *
     * <p>
     * The token is composed of the prefix "V" followed by a sequence of five
     * random characters. Characters are selected randomly from the ASCII range
     * between uppercase 'A' (65) and lowercase 'z' (122), which includes
     * uppercase letters, lowercase letters, and some punctuation characters.
     *
     * @return A generated verification token in the format "Vxxxxx" where xxxxx
     * are random characters.
     */
    String generateVerificationToken();

    /**
     * Generates a unique admin token.
     *
     * <p>
     * The token is composed of the prefix "A" followed by a sequence of five
     * random characters. Characters are selected randomly from the ASCII range
     * between uppercase 'A' (65) and lowercase 'z' (122), which includes
     * uppercase letters, lowercase letters, and some punctuation characters.
     *
     * @return A generated admin token in the format "Axxxxx" where xxxxx
     * are random characters.
     */
    String generateAdminToken();

    /**
     * Generates a unique instructor token.
     *
     * <p>
     * The token is composed of the prefix "I" followed by a sequence of five
     * random characters. Characters are selected randomly from the ASCII range
     * between uppercase 'A' (65) and lowercase 'z' (122), which includes
     * uppercase letters, lowercase letters, and some punctuation characters.
     *
     * @return A generated instructor token in the format "Ixxxxx" where xxxxx
     * are random characters.
     */

    String generateInstructorToken();

    User promoteUser(User user) throws SQLException, Exception;
            
    /**

     * Attempts to log in a user by verifying the provided email and password.
     *
     * @param user A User object containing the email and plain-text password
     * for login.
     * @return The authenticated User object if login is successful.
     * @throws SQLException If a database access error occurs.
     * @throws Exception If the user email is not recognized or if the password
     * is invalid.
     * @throws IllegalArgumentException If the user, email, or password is null.
     */
    User LogIn(User user) throws SQLException, Exception;

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
     * Promotes a user to the role of contractor.
     *
     * <p>
     * This method updates the user's role to {@link User.Role#CONTRACTOR} and
     * saves the update in the database. If the promotion fails, an exception is
     * thrown.
     *
     * @param user The {@link User} object representing the user to be promoted.
     * @return The updated {@link User} object with the new role.
     * @throws SQLException if an error occurs during database access.
     * @throws Exception if the promotion fails or if the user is null.
     * @throws IllegalArgumentException if the user is null.
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
    
    User PromoteStaff(User user) throws Exception;
}
