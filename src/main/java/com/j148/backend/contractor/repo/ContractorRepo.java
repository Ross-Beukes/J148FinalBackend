package com.j148.backend.contractor.repo;

import com.j148.backend.contract_period.model.ContractPeriod;
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
     * @param contractorId the unique identifier of the contractor
     * @return an Optional containing the found contractor if it exists, otherwise an empty Optional
     * @throws SQLException if a database access error occurs
     */
    Optional<Contractor> findById(Long contractorId) throws SQLException;

    /**
     * Updates an existing contractor record in the database.
     *
     * @param contractor the contractor with updated values
     * @return an Optional containing the updated contractor if successful, otherwise an empty Optional
     * @throws SQLException if a database access error occurs
     */
    Optional<Contractor> update(Contractor contractor) throws SQLException;

    /**
     * Retrieves all contractors from the database.
     *
     * @return a list of all contractors
     * @throws SQLException if a database access error occurs
     */
    List<Contractor> findAll() throws SQLException;
    
    /**
 * Finds contractors based on the specified contract period.
 *
 * <p>This method performs a database query to retrieve all contractors associated with the given 
 * contract period ID. It populates a list of {@link Contractor} objects with details from the 
 * `contractor` table, including the contractor's ID, status, associated user, and contract period.
 *
 * @param contractPeriod The {@link ContractPeriod} object containing the ID of the contract period 
 *                       to search for. The `contractPeriodId` attribute must be set.
 * @return A {@link List} of {@link Contractor} objects representing the contractors currently 
 *         associated with the specified contract period.
 * @throws SQLException if there is an error connecting to the database or executing the SQL statement.
 */
    List<Contractor> findCurrentContractor(ContractPeriod contractPeriod) throws SQLException;
}


