/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.user.repo;

import com.j148.backend.user.model.User;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 * @author glenl
 */
public interface UserRepo {

    /**
     * Registers a new user in the database.
     *
     * <p>
     * This method inserts a new user record into the `user` table in the
     * database. It uses a prepared statement to set each field in the query,
     * and the `auto-generated` user ID is retrieved if the insertion is
     * successful. The method uses a save point to allow for a rollback in case
     * the insertion fails, ensuring database integrity.
     *
     * @param user The {@link User} object containing user details to be
     * registered. It must include the following attributes: name, surname,
     * email, gender, ID number, role, race, location, age, and password.
     * @return An {@link Optional} containing the {@link User} object with an
     * assigned user ID if the registration is successful. If the registration
     * fails, it returns an empty Optional.
     * @throws SQLException if there is an error connecting to the database or
     * executing the SQL statement.
     */
    Optional<User> register(User user) throws SQLException;

    /**
     * Updates an existing user's details in the database.
     *
     * <p>
     * This method updates the specified fields of an existing user in the
     * `user` table, identified by their ID number. The update operation is
     * performed within a transaction, with a savepoint set to allow for a
     * rollback if the update fails. If the update is successful, the method
     * commits the changes and returns the updated {@link User} object.
     *
     * @param user The {@link User} object containing updated user details. This
     * object must include the name, surname, email, gender, location, password,
     * and ID number of the user to be updated.
     * @return An {@link Optional} containing the updated {@link User} object if
     * the update is successful. If the update fails or no records are affected,
     * it returns an empty Optional.
     * @throws SQLException if there is an error connecting to the database or
     * executing the SQL statement.
     */
    Optional<User> updateUser(User user) throws SQLException;

    /**
     * Retrieves a user's details from the database based on their email
     * address.
     *
     * <p>
     * This method performs a database query to find a user in the `user` table
     * using the specified email address. If a user is found, it populates the
     * provided {@link User} object with the details retrieved from the
     * database.
     *
     * @param user The {@link User} object containing the email address to
     * search for. This object must have the email attribute set.
     * @return An {@link Optional} containing the {@link User} object with full
     * details if a matching user is found. If no user is found or an error
     * occurs, returns an empty Optional.
     * @throws SQLException if there is an error connecting to the database or
     * executing the SQL statement.
     */
    Optional<User> retreiveUserFromEmail(User user) throws SQLException;

    /**
     * Promotes an applicant by updating their role in the database.
     *
     * <p>
     * This method updates the role of an existing user in the `user` table,
     * identified by their ID number. The new role is specified in the
     * {@link User} object passed as a parameter. The update is performed within
     * a transaction, with a save point set to allow for rollback in case of
     * failure. If the update is successful, the transaction is committed, and
     * the method returns the updated {@link User} object.
     *
     * @param user The {@link User} object containing the new role and ID number
     * of the applicant to be promoted. The role and ID number must be set in
     * the object.
     * @return An {@link Optional} containing the {@link User} object with the
     * updated role if the promotion is successful. If the promotion fails or no
     * records are affected, returns an empty Optional.
     * @throws SQLException if there is an error connecting to the database or
     * executing the SQL statement.
     */
    Optional<User> promoteApplicant(User user) throws SQLException;

    /**
     * Retrieves a user's details from the database based on their user ID.
     *
     * <p>
     * This method performs a database query to find a user in the `user` table
     * using the specified user ID. If a user is found, it populates the
     * provided {@link User} object with the details retrieved from the
     * database, including fields such as name, surname, email, gender, role,
     * and others.
     *
     * @param user The {@link User} object containing the user ID to search for.
     * This object must have the user ID attribute set.
     * @return An {@link Optional} containing the {@link User} object with full
     * details if a matching user is found. If no user is found or an error
     * occurs, returns an empty Optional.
     * @throws SQLException if there is an error connecting to the database or
     * executing the SQL statement.
     */
    Optional<User> retreiveUserFromUserID(User user) throws SQLException;
}
