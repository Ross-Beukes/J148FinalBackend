/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contractor.service;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author glenl
 */
public interface ContractorService {

    /**
     * Retrieves the list of contractors with an active contract in the current
     * contract period.
     *
     * <p>
     * This method first obtains the current contract period from the
     * {@link ContractPeriodService}. It then queries the contractor repository
     * to fetch contractors associated with this contract period.
     *
     * @return A list of {@link Contractor} objects representing the contractors
     * with active contracts in the current contract period.
     * @throws SQLException if there is an error accessing the database when
     * retrieving the contract period or the contractors.
     * @throws Exception if the current contract period cannot be found.
     */
    List<Contractor> findCurrentContractors() throws SQLException, Exception;
}
