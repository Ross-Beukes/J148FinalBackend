package com.j148.backend.contractor_performance.service;

import com.j148.backend.contractor_performance.model.ContractorPerformance;
import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.ContractorPerformanceNotFoundException;
import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.user.model.User;
import java.io.IOException;

import java.sql.SQLException;
import java.util.List;

/**
 * Service interface for managing contractor performance data.
 * This interface defines methods to retrieve, filter, and validate contractor performance information.
 */
public interface ContractorPerformanceService {

    /**
     * Retrieves the performance details for a specific contractor, based on the given user.
     * The user must represent a valid contractor in the system.
     *
     * @param user The user object representing the contractor whose performance details are to be retrieved.
     * @return A {@link ContractorPerformance} object containing the performance details of the contractor.
     * @throws ContractorPerformanceNotFoundException If no performance data is found for the contractor.
     * @throws UserNotFoundException If the provided user does not exist or has an invalid ID.
     * @throws ContractorNotFoundException If the contractor associated with the user is not found.
     * @throws SQLException If an error occurs while accessing the database.
     */
    ContractorPerformance getContractorPerformance(User user) 
            throws ContractorPerformanceNotFoundException, UserNotFoundException, ContractorNotFoundException, SQLException;

    /**
     * Retrieves performance details for all contractors in the system.
     * 
     * @return A list of {@link ContractorPerformance} objects representing the performance data of all contractors.
     * @throws ContractorPerformanceNotFoundException If no contractor performance data is available.
     * @throws UserNotFoundException If any user in the list does not exist or has an invalid ID.
     * @throws ContractorNotFoundException If any contractor in the list is not found.
     * @throws SQLException If an error occurs while accessing the database.
     */
    List<ContractorPerformance> getAllContractorPerformance() 
            throws ContractorPerformanceNotFoundException, UserNotFoundException, ContractorNotFoundException, SQLException;

    /**
     * Filters contractor performance data based on the provided filters.
     * The filters are applied to a list of contractor performances and return only those that match the criteria.
     *
     * @param filters A string representing the filtering criteria to be applied to the contractor performance data.
     * @param cp The list of {@link ContractorPerformance} objects to filter.
     * @return A filtered list of {@link ContractorPerformance} objects.
     * @throws ContractorPerformanceNotFoundException If the list of contractor performances is empty or null.
     * @throws UserNotFoundException If any user in the list does not exist or has an invalid ID.
     * @throws ContractorNotFoundException If any contractor in the list is not found.
     * @throws SQLException If an error occurs while accessing the database.
     */
    List<ContractorPerformance> filterContractorPerformance(String filters, List<ContractorPerformance> cp)
            throws ContractorPerformanceNotFoundException, UserNotFoundException, ContractorNotFoundException, SQLException;

    /**
     * Retrieves performance data for all contractors in the system.
     * This method is similar to {@link #getAllContractorPerformance}, but may be used with different requirements or contexts.
     *
     * @return A list of {@link ContractorPerformance} objects representing the performance data of all contractors.
     * @throws ContractorPerformanceNotFoundException If no contractor performance data is available.
     * @throws UserNotFoundException If any user in the list does not exist or has an invalid ID.
     * @throws ContractorNotFoundException If any contractor in the list is not found.
     * @throws SQLException If an error occurs while accessing the database.
     */
    List<ContractorPerformance> getAllContractors() 
            throws ContractorPerformanceNotFoundException, UserNotFoundException, ContractorNotFoundException, SQLException;
    
    boolean downloadReportFile(List<ContractorPerformance> contractorPerformanceList) throws SQLException, IOException;
}
