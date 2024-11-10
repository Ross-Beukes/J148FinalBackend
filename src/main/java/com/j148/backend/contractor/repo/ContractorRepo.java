package com.j148.backend.contractor.repo;

import com.j148.backend.contractor.model.Contractor;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ContractorRepo {

    /**
     * Saves a new contractor record to the database.
     *
     * @param contractor the contractor to be saved
     * @return an Optional containing the saved contractor if successful, otherwise an empty Optional
     * @throws SQLException if a database access error occurs
     */
    Optional<Contractor> save(Contractor contractor) throws SQLException;

    /**
     * Finds a contractor by its unique identifier.
     *
     * @param contractor the unique identifier of the contractor
     * @return an Optional containing the found contractor if it exists, otherwise an empty Optional
     * @throws SQLException if a database access error occurs
     */
    Optional<Contractor> findByID(Contractor contractor) throws SQLException;

    /**
     * Updates an existing contractor record in the database.
     *
     * @param contractor the contractor with updated values
     * @return an Optional containing the updated contractor if successful, otherwise an empty Optional
     * @throws SQLException if a database access error occurs
     */
    Optional<Contractor> updateStatus(Contractor contractor) throws SQLException;

    /**
     * Retrieves all contractors from the database.
     *
     * @return a list of all contractors
     * @throws SQLException if a database access error occurs
     */
    List<Contractor> findAll() throws SQLException;
}