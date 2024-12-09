package com.j148.backend.contractor_performance.repo;

import com.j148.backend.contractor_performance.model.ContractorPerformance;
import com.j148.backend.user.model.User;
import java.io.ByteArrayOutputStream;

import java.io.IOException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing contractor performance data.
 * This interface provides methods to retrieve and filter contractor performance
 * data, including details about warnings, attendance, and hearings.
 */
public interface ContractorPerformanceRepo {

    /**
     * Retrieves the performance data for a specific contractor based on the
     * provided user. This data includes information on warnings, attendance
     * records, and hearings.
     *
     * @param user the user object representing the contractor whose performance
     * data is needed.
     * @return an Optional containing the contractor performance data if
     * available, or an empty Optional if no data is found for the provided
     * user.
     * @throws SQLException if a database access error occurs while fetching the
     * data.
     */
    Optional<ContractorPerformance> getContractorPerformance(User user) throws SQLException;

    /**
     * Retrieves performance data for all contractors. The data includes
     * warnings, attendance records, and hearing details for each contractor.
     *
     * @return a list of contractor performance records for all contractors.
     * @throws SQLException if a database access error occurs while fetching the
     * data.
     */
    List<ContractorPerformance> getAllContractorPerformance() throws SQLException;

    /**
     * Filters the list of contractor performance data based on the provided
     * filters. The filters are applied to various fields such as attendance,
     * warnings, and contractor status.
     *
     * @param filters a comma-separated string containing filter conditions
     * (e.g., "attendance-time-in=2022-01-01").
     * @param cp a list of contractor performance records to be filtered.
     * @return a filtered list of contractor performance records that match the
     * specified filter conditions.
     * @throws SQLException if a database access error occurs while filtering
     * the data.
     */
    List<ContractorPerformance> filterContractorPerformance(String filters, List<ContractorPerformance> cp) throws SQLException;

    /**
     * Retrieves a list of all contractors. This method fetches basic
     * information about all contractors, including their status and related
     * user details.
     *
     * @return a list of contractor performance records, which contain
     * information about contractors and their users.
     * @throws SQLException if a database access error occurs while fetching the
     * data.
     */
    List<ContractorPerformance> getAllContractors() throws SQLException;
}