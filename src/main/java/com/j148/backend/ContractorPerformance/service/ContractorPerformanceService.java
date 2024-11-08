package com.j148.backend.ContractorPerformance.service;

import com.j148.backend.ContractorPerformance.model.ContractorPerformance;
import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.ContractorPerformanceNotFoundException;
import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.user.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ContractorPerformanceService {

    /**
     * Retrieves the performance details for a specific contractor.
     *
     * @param user The user object representing the Contractor.
     * @return Optional containing ContractorPerformance if found or empty if not.
     */
    ContractorPerformance getContractorPerformance(User user) throws ContractorPerformanceNotFoundException, UserNotFoundException, ContractorNotFoundException, SQLException;

    /**
     * Retrieves performance details for all contractors.
     *
     * @return List of ContractorPerformance objects for all contractors
     */
    List<ContractorPerformance> getAllContractorPerformance() throws Exception;

}
