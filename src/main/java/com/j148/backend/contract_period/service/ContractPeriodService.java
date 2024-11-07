/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contract_period.service;

import com.j148.backend.contract_period.model.ContractPeriod;
import java.sql.SQLException;

/**
 *
 * @author glenl
 */
public interface ContractPeriodService {

    /**
     * Retrieves the current contract period based on today's date.
     *
     * <p>
     * This method uses the repository to fetch the active contract period where
     * today's date falls within the start and end dates of the contract period.
     * If no current contract period is found, an exception is thrown.
     *
     * @return The {@link ContractPeriod} object representing the active
     * contract period.
     * @throws SQLException if there is an error accessing the database while
     * retrieving the contract period.
     * @throws Exception if no active contract period is found in the database.
     */
    ContractPeriod getCurrentContractPeriod() throws SQLException, Exception;
}
