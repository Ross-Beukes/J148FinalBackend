/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contractor.service;

import com.j148.backend.contract_period.service.ContractPeriodService;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.user.model.User;
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

    /**
     * Promotes a user to a contractor and assigns them to the next available
     * contract period.
     *
     * <p>
     * This method changes the role of a user to a contractor, assigns them
     * active status, and associates them with the next available contract
     * period. If no future contract period is found, an exception is thrown.
     *
     * @param user The user to be promoted.
     * @return The newly created contractor.
     * @throws SQLException if a database access error occurs.
     * @throws Exception if the promotion or save process fails.
     */
    Contractor promoteToContractor(User user) throws SQLException, Exception;

    /**
     * Updates the status of an existing contractor to EXTERNAL.
     *
     * @param contractor The contractor to be updated.
     * @return The updated contractor with EXTERNAL status.
     * @throws SQLException if a database access error occurs.
     * @throws Exception if the update process fails.
     */
    Contractor promoteToExternalContractor(Contractor contractor) throws SQLException, Exception;
}
