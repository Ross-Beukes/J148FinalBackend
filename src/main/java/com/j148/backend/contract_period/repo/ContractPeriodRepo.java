package com.j148.backend.contract_period.repo;

import java.sql.SQLException;
import java.util.Optional;
/**
 * Repository interface for managing contract periods.
 * Provides methods to add, find and update contract periods in the database.
 */
public interface ContractPeriodRepo {
    /**
     * Adds a new contract period to the database.
     *
     * @param contractPeriod the contract period to be added
     * @return an Optional containing the added contract period, if successful
     * @throws SQLException if there is an error accessing the database
     */
    Optional<com.j148.backend.contract_period.model.ContractPeriod> saveContractPeriod(com.j148.backend.contract_period.model.ContractPeriod contractPeriod) throws SQLException;
    /**
     * Finds a contract period by its name.
     *
     * @param name the name of the contract period to be retrieved
     * @return an Optional containing the contract period if found, or an empty Optional if not
     * @throws SQLException if there is an error accessing the database
     */
    Optional<com.j148.backend.contract_period.model.ContractPeriod> findContractPeriodByName(String name) throws SQLException;
    /**
     * Finds a contract period by its name.
     *
     * @param contractPeriodId the id of the contract period to be retrieved
     * @return an Optional containing the contract period if found, or an empty Optional if not
     * @throws SQLException if there is an error accessing the database
     */

    Optional<ContractPeriod> findContractPeriodById(long contractPeriodId) throws SQLException;
    /**
     * Updates an existing contract period in the database.
     *
     * @param contractPeriod the contract period with updated information
     * @return an Optional containing the updated contract period, if successful
     * @throws SQLException if there is an error accessing the database
     */
    Optional<com.j148.backend.contract_period.model.ContractPeriod> updateContractPeriod(com.j148.backend.contract_period.model.ContractPeriod contractPeriod) throws SQLException;
    
}
