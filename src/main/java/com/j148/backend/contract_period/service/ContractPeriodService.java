package com.j148.backend.contract_period.service;

import com.j148.backend.contract_period.model.ContractPeriod;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    /**
     * Retrieves the next contract period starting after the current date.
     *
     * <p>
     * This method queries the database for the next available contract period
     * where the start date is greater than the current date. If a matching
     * contract period is found, it returns an {@link Optional} containing the
     * {@link ContractPeriod} object. If no matching contract period is found,
     * an empty {@link Optional} is returned.
     *
     * @return An {@link Optional} containing the next {@link ContractPeriod} if
     * found; otherwise, an empty {@link Optional}.
     * @throws SQLException if a database access error occurs.
     */
    ContractPeriod getNextContractPeriod() throws SQLException, Exception;

    /**
     * Calculates the average enrollment over a specified period of years.
     *
     * <p>
     * This method queries the database to count the number of enrollments
     * between the specified start and end years (inclusive), then calculates
     * the average by dividing the total enrollments by the number of years
     * in the specified period.
     * </p>
     *
     * @param startYear The beginning year of the period.
     * @param endYear The ending year of the period.
     * @return The average number of enrollments per year over the specified period.
     * @throws SQLException if a database access error occurs.
     */
    double enrollmentAverageForPeriodOfYears(int startYear, int endYear) throws SQLException;

    /**
     * Calculates the average enrollment for a specified year.
     *
     * <p>
     * This method queries the database to count the number of enrollments
     * in the specified year and returns it as a yearly average.
     * </p>
     *
     * @param year The year for which to calculate the enrollment average.
     * @return The average number of enrollments for the specified year.
     * @throws SQLException if a database access error occurs.
     */
    double enrollmentAveragesForYear(int year) throws SQLException;

    List<ContractPeriod> getAllFutureContractPeriods() throws SQLException, Exception;
}
