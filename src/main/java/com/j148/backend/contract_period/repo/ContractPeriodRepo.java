package com.j148.backend.contract_period.repo;

import com.j148.backend.contract_period.model.ContractPeriod;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Repository interface for managing contract periods. Provides methods to add,
 * find and update contract periods in the database.
 */
public interface ContractPeriodRepo {

    /**
     * Adds a new contract period to the database.
     *
     * @param contractPeriod the contract period to be added
     * @return an Optional containing the added contract period, if successful
     * @throws SQLException if there is an error accessing the database
     */
    Optional<ContractPeriod> saveContractPeriod(com.j148.backend.contract_period.model.ContractPeriod contractPeriod) throws SQLException;

    /**
     * Finds a contract period by its name.
     *
     * @param name the name of the contract period to be retrieved
     * @return an Optional containing the contract period if found, or an empty
     * Optional if not
     * @throws SQLException if there is an error accessing the database
     */
    Optional<ContractPeriod> findContractPeriodByName(String name) throws SQLException;

    /**
     * Updates an existing contract period in the database.
     *
     * @param contractPeriod the contract period with updated information
     * @return an Optional containing the updated contract period, if successful
     * @throws SQLException if there is an error accessing the database
     */
    Optional<ContractPeriod> updateContractPeriod(com.j148.backend.contract_period.model.ContractPeriod contractPeriod) throws SQLException;

    /**
     * Retrieves the current contract period based on today's date.
     *
     * <p>
     * This method queries the database for a contract period where today's date
     * falls between the start and end dates of the contract. If a matching
     * contract period is found, it populates and returns a
     * {@link ContractPeriod} object containing the contract's details.
     *
     * @return An {@link Optional} containing the {@link ContractPeriod} object
     * if a current contract period is found; otherwise, an empty Optional if no
     * matching contract period exists.
     * @throws SQLException if there is an error connecting to the database or
     * executing the SQL statement.
     */
    Optional<ContractPeriod> getCurrentContractPeriod() throws SQLException;

    /**
     * Retrieves the next contract period where the start date is after the
     * current date.
     *
     * <p>
     * This method checks the database for a contract period that starts after
     * today’s date and returns it if found.
     *
     * @return An Optional containing the next contract period if it exists,
     * otherwise Optional.empty().
     * @throws SQLException if a database access error occurs.
     */
    Optional<ContractPeriod> getNextContractPeriod() throws SQLException;
}
