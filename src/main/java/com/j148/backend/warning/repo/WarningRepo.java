package com.j148.backend.warning.repo;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.warning.model.Warning;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Warning entities in the system.
 * Provides methods for creating, retrieving, and updating warnings associated with contractors.
 */
public interface WarningRepo {
    /**
     * Saves a new warning to the database.
     *
     * @param warning the Warning entity to be saved
     * @return Optional containing the saved Warning with generated ID if successful, empty Optional otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<Warning> save(Warning warning) throws SQLException;

    /**
     * Retrieves a warning by its ID, including associated contractor, user, and contract period information.
     *
     * @param warning the Warning entity containing the ID to search for
     * @return Optional containing the found Warning if it exists, empty Optional otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<Warning> findById(Warning warning) throws SQLException;

    /**
     * Retrieves all warnings associated with a specific contractor.
     *
     * @param contractor the Contractor entity to find warnings for
     * @return Optional containing a List of all warnings for the contractor, empty Optional if none found
     * @throws SQLException if a database access error occurs
     */
    Optional<List<Warning>> findByContractor(Contractor contractor) throws SQLException;

    /**
     * Retrieves all active warnings for a specific contractor.
     * Active warnings are those with a state of 'ACTIVE'.
     *
     * @param contractor the Contractor entity to find active warnings for
     * @return Optional containing a List of active warnings for the contractor, empty Optional if none found
     * @throws SQLException if a database access error occurs
     */
    Optional<List<Warning>> findAllActiveByContractor(Contractor contractor) throws SQLException;

    /**
     * Retrieves all appealed warnings for a specific contractor.
     * Appealed warnings are those with a state of 'APPEALED'.
     *
     * @param contractor the Contractor entity to find appealed warnings for
     * @return Optional containing a List of appealed warnings for the contractor, empty Optional if none found
     * @throws SQLException if a database access error occurs
     */
    Optional<List<Warning>> findAppealedByContractor(Contractor contractor) throws SQLException;

    /**
     * Updates the state of an existing warning.
     *
     * @param warning the Warning entity containing the new state and warning ID
     * @return Optional containing the updated Warning if successful, empty Optional otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<Warning> updateState(Warning warning) throws SQLException;

    /**
     * Creates a new warning with reason 'LATE' for a specific contractor.
     * The warning is created with the current timestamp and ACTIVE state.
     *
     * @param contractor the Contractor entity to create the late warning for
     * @return Optional containing the created Warning if successful, empty Optional otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<Warning> createLateWarning(Contractor contractor) throws SQLException;

    /**
     * Retrieves all warnings issued within a specified date range.
     *
     * @param startDate the start of the date range (inclusive)
     * @param endDate the end of the date range (inclusive)
     * @return Optional containing a List of warnings within the date range, empty Optional if none found
     * @throws SQLException if a database access error occurs
     */
    Optional<List<Warning>> findWarningsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;

    /**
     * Retrieves all final warnings for a specific contractor.
     * Final warnings are those with a state of 'FINAL'.
     *
     * @param contractor the Contractor entity to find final warnings for
     * @return Optional containing a List of final warnings for the contractor, empty Optional if none found
     * @throws SQLException if a database access error occurs
     */
    Optional<List<Warning>> findFinalWarningsByContractor(Contractor contractor) throws SQLException;

    /**
     * Counts the number of active warnings for a specific contractor.
     *
     * @param contractor the Contractor entity to count active warnings for
     * @return Optional containing the count of active warnings, Optional of 0 if none found
     * @throws SQLException if a database access error occurs
     */
    Optional<Long> countActiveWarningsByContractor(Contractor contractor) throws SQLException;

    /**
     * Retrieves all warnings with a specific reason.
     *
     * @param reason the WarningReason to search for
     * @return Optional containing a List of warnings with the specified reason, empty Optional if none found
     * @throws SQLException if a database access error occurs
     */
    Optional<List<Warning>> findWarningsByReason(Warning warning) throws SQLException;

    /**
     * Checks if a warning exists for a specific contractor on a given date.
     * The comparison is done on the date portion only, ignoring time.
     *
     * @param contractor the Contractor entity to check warnings for
     * @param dateIssue the date to check for existing warnings
     * @return Optional containing true if a warning exists, false otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<Boolean> existsByContractorAndDateIssue(Contractor contractor, LocalDateTime dateIssue) throws SQLException;
}