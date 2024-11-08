package com.j148.backend.ContractorPerformance.service;

import com.j148.backend.ContractorPerformance.model.ContractorPerformance;
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
     * @return Optional containing ContractorPerformance if found or empty if not.*/
    Optional<ContractorPerformance> getContractorPerformance(User user)throws UserNotFoundException, SQLException;
/**
 * Retrieves performance details for all contractors.
 *
 * @return List of ContractorPerformance objects for all contractors*/
List<ContractorPerformance>getAllContractorPerformance()throws Exception;
/**
 * Validates that ContractorPerformance has all necessary components, such as User and Contractor details.
 *
 * @param  contractorPerformance The ContractorPerformance object to be validated.
 * @return true if valid, false if otherwise.*/
boolean validateContractorPerformance(ContractorPerformance contractorPerformance);

}
