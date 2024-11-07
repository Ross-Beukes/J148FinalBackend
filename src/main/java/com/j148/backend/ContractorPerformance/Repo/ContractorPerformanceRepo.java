package com.j148.backend.ContractorPerformance.Repo;

import com.j148.backend.ContractorPerformance.model.ContractorPerformance;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.user.model.User;
import com.j148.backend.warning.model.Warning;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ContractorPerformanceRepo {
    
    /**
     * Retrieves the performance data for a specific contractor based on the provided user.
     * This data includes information on warnings, attendance records, and hearings.
     *
     * @param user the user object representing the contractor whose performance data is needed.
     * @return an Optional containing the contractor performance data if available, or an empty Optional if not found.
     * @throws SQLException if a database access error occurs.
     */
    Optional<ContractorPerformance> getContractorPerformance(User user) throws SQLException;

    /**
     * Retrieves performance data for all contractors.
     * The data includes warnings, attendance records, and hearing details for each contractor.
     *
     * @return a list of contractor performance records for all contractors.
     * @throws SQLException if a database access error occurs.
     */
    List<ContractorPerformance> getAllContractorPerformance() throws SQLException;
    
    List<ContractorPerformance> filterContractorPerformance(String filters, List<ContractorPerformance> cp);
}
