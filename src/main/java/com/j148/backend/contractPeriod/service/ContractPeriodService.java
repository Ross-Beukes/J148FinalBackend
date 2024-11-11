package com.j148.backend.contractPeriod.service;
import com.j148.backend.contract_period.model.ContractPeriod;

public interface ContractPeriodService {
    /**
     * Saves a new ContractPeriod to the database.
     *
     * @param contractPeriod The ContractPeriod object to be saved.
     * @return The saved ContractPeriod object with generated ID if successful.
     * @throws Exception If there is an error during the save operation, including database connectivity issues.
     */
    public ContractPeriod saveContractPeriod(ContractPeriod contractPeriod) throws Exception;
    /**
     * Finds a ContractPeriod by its name.
     *
     * @param name The name of the ContractPeriod to search for.
     * @return The ContractPeriod object if found.
     * @throws Exception If no ContractPeriod with the specified name exists or if there is a database error.
     */
    public ContractPeriod findContractPeriodByName(String name) throws Exception;
    /**
     * Finds a ContractPeriod by its ID.
     *
     * @param contractPeriod The ContractPeriod object containing the ID to search for.
     * @return The ContractPeriod object if found.
     * @throws Exception If no ContractPeriod with the specified ID exists or if there is a database error.
     */
    public ContractPeriod findContractPeriodById(ContractPeriod contractPeriod) throws Exception;
    /**
     * Updates an existing ContractPeriod in the database.
     *
     * @param contractPeriod The ContractPeriod object with updated fields.
     * @return The updated ContractPeriod object if the update is successful.
     * @throws Exception If there is an error during the update operation or if the ContractPeriod does not exist.
     */
    public ContractPeriod updateContractPeriod(ContractPeriod contractPeriod) throws Exception;
}
