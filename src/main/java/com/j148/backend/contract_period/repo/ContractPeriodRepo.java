package com.j148.backend.contract_period.repo;

import com.j148.backend.contract_period.model.ContractPeriod;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Repository interface for managing contract periods. Provides methods to add,
 * find and update contract periods in the database.
 */
public interface ContractPeriodRepo {
    /**
     * Adds a new contract period to the database.
     *
     * @param contractPeriod the contract period to be added
     * @return an Optional containing the added contract period, if successful
     * @throws SQLException if there is an error accessing the database
     */
    Optional<ContractPeriod> saveContractPeriod(ContractPeriod contractPeriod) throws SQLException;

    /**
     * Finds a contract period by its name.
     *
     * @param name the name of the contract period to be retrieved
     * @return an Optional containing the contract period if found, or an empty
     * Optional if not
     * @throws SQLException if there is an error accessing the database
     */
    Optional<ContractPeriod> findContractPeriodByName(String name) throws SQLException;

    /**
     * Calculates the average number of enrollments for a specific year.
     *
     * @param year The year for which to calculate the average enrollment.
     * @return The average enrollment count for the specified year.
     * @throws SQLException If a database access error occurs.
     */
    double enrollmentAveragesForYear(int year) throws SQLException;

    /**
     * Calculates the average number of enrollments over a period of years.
     *
     * @param startYear The starting year of the period.
     * @param endYear The ending year of the period.
     * @return The average enrollment count per year for the specified period.
     * @throws SQLException If a database access error occurs.
     */

    double enrollmentAverageForPeriodOfYears(int startYear, int endYear) throws SQLException;

    /**
     * Updates an existing contract period in the database.
     *
     * @param contractPeriod the contract period with updated information
     * @return an Optional containing the updated contract period, if successful
     * @throws SQLException if there is an error accessing the database
     */
    Optional<ContractPeriod> updateContractPeriod(ContractPeriod contractPeriod) throws SQLException;

    /**
     * Retrieves the current contract period based on today's date.
     *
     * <p>
     * This method queries the database for a contract period where today's date
     * falls between the start and end dates of the contract. If a matching
     * contract period is found, it populates and returns a
     * {@link ContractPeriod} object containing the contract's details.
     *
     * @return An {@link Optional} containing the {@link ContractPeriod} object
     * if a current contract period is found; otherwise, an empty Optional if no
     * matching contract period exists.
     * @throws SQLException if there is an error connecting to the database or
     * executing the SQL statement.
     */
    Optional<ContractPeriod> getCurrentContractPeriod() throws SQLException;

    /**
     * Retrieves the next contract period where the start date is after the
     * current date.
     *
     * <p>
     * This method checks the database for a contract period that starts after
     * today’s date and returns it if found.
     *
     * @return An Optional containing the next contract period if it exists,
     * otherwise Optional.empty().
     * @throws SQLException if a database access error occurs.
     */
    Optional<ContractPeriod> getNextContractPeriod() throws SQLException;
    /**
     * Retrieves a {@link ContractPeriod} record from the database based on the provided {@code contractPeriod}.
     *
     * <p>This method takes a {@link ContractPeriod} object containing an ID and attempts to retrieve
     * the corresponding record from the database. If a match is found, an {@link Optional} containing
     * the record is returned. If no record matches the given ID, an empty {@link Optional} is returned.</p>
     *
     * @param contractPeriod the {@link ContractPeriod} object containing the ID of the contract period to find
     * @return an {@link Optional} containing the found {@link ContractPeriod}, or an empty {@link Optional} if no record is found
     * @throws SQLException if a database access error occurs
     */
    Optional<ContractPeriod> findById(ContractPeriod contractPeriod) throws SQLException;
}
