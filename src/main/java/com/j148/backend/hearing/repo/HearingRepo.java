/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.hearing.repo;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author arshr
 */
/**
 * Interface for data access operations related to Hearing entities.
 * Defines methods for creating, updating, retrieving, and querying hearings.
 */
public interface HearingRepo {

    /**
     * Creates a new hearing record in the database.
     *
     * @param hearing The Hearing object containing details for the new record.
     * @return An Optional containing the created Hearing if successful, or an empty Optional if the creation fails.
     * @throws SQLException If a database access error occurs.
     */
    Optional<Hearing> createHearing(Hearing hearing) throws SQLException;

    /**
     * Updates an existing hearing record in the database based on provided details.
     *
     * @param hearing The Hearing object with updated details for the record.
     * @return An Optional containing the updated Hearing if successful, or an empty Optional if the update fails.
     * @throws SQLException If a database access error occurs.
     */
    Optional<Hearing> updateHearing(Hearing hearing) throws SQLException;

    /**
     * Retrieves a specific hearing record from the database based on hearing ID.
     *
     * @param hearing A Hearing object containing the ID of the hearing to be retrieved.
     * @return An Optional containing the retrieved Hearing if found, or an empty Optional if no matching record exists.
     * @throws SQLException If a database access error occurs.
     */
    Optional<Hearing> getHearing(Hearing hearing) throws SQLException;

    /**
     * Retrieves all hearing records from the database.
     *
     * @return A List of all Hearing objects in the database.
     * @throws SQLException If a database access error occurs.
     */
    List<Hearing> findAllHearings() throws SQLException;

    /**
     * Retrieves all hearing records associated with a specific contractor.
     *
     * @param hearing A Hearing object containing the contractor's ID.
     * @return A List of Hearing objects linked to the specified contractor.
     * @throws SQLException If a database access error occurs.
     */
    List<Hearing> findAllHearingsForIndividual(Hearing hearing) throws SQLException;

    /**
     * Retrieves all upcoming hearings, specifically those scheduled for dates in the future.
     *
     * @return A List of Hearing objects representing all upcoming hearings, ordered by schedule date.
     * @throws SQLException If a database access error occurs.
     */
    List<Hearing> findUpcomingHearings() throws SQLException;

    /**
     * Retrieves all hearing records scheduled within a specific date range.
     *
     * @param startDate The start of the date range.
     * @param endDate The end of the date range.
     * @return A List of Hearing objects scheduled between the specified dates.
     * @throws SQLException If a database access error occurs.
     */
    List<Hearing> findHearingsWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
     /**
     * Retrieves all hearing records for a particular Contractor.
     *
     * @param contractor
     *
     * @return A List of Hearing objects
     * @throws SQLException If a database access error occurs.
     */
    List<Hearing> findContractorHearingHistory(Contractor contractor) throws SQLException;
}
