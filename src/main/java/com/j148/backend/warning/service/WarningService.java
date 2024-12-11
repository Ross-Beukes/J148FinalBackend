package com.j148.backend.warning.service;

import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.WarningNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.warning.model.Warning;
import jakarta.transaction.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface WarningService {

    /**
     * Issues a warning to a contractor for late arrival.
     *
     * @param contractor The Contractor object to issue the warning to.
     * @return A Warning object representing the issued warning for late
     * arrival.
     * @throws SQLException If a database access error occurs.
     * @throws Exception If the warning could not be created or the contractor
     * is not found.
     * @throws IllegalArgumentException If the contractor is null.
     */
    Warning lateComingWarning(Contractor contractor) throws SQLException, Exception;

    /**
     * Issues a warning to a contractor for being absent.
     *
     * @param contractor The Contractor object to issue the warning to.
     * @return A Warning object representing the issued warning for absence.
     * @throws SQLException If a database access error occurs.
     * @throws Exception If the warning could not be created or the contractor
     * is not found.
     * @throws IllegalArgumentException If the contractor is null.
     */
    Warning absentWarning(Contractor contractor) throws SQLException, Exception;

    Warning save(Warning warning) throws SQLException, WarningNotFoundException, ContractorNotFoundException;



    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    Warning appealWarning(Warning warning, Contractor contractor) throws Exception;

    Warning findById(Warning warning) throws SQLException;

    List<Warning> findByContractor(Contractor contractor) throws SQLException;

    List<Warning> findAllActiveByContractor(Contractor contractor) throws SQLException, Exception;

    List<Warning> findAppealedByContractor(Contractor contractor) throws SQLException;

    Warning updateState(Warning warning) throws SQLException, Exception;

    Warning createLateWarning(Contractor contractor) throws SQLException;

    List<Warning> findWarningsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;

    List<Warning> findFinalWarningsByContractor(Contractor contractor) throws SQLException;

    Optional<Long> countActiveWarningsByContractor(Contractor contractor) throws SQLException;

    List<Warning> findWarningsByReason(Warning warning) throws SQLException;

    Boolean existsByContractorAndDateIssue(Contractor contractor, LocalDateTime dateIssue) throws SQLException;

       /**
     * Accesses the Warning Repository and returns a list of all warnings from the database that have been Appealed
     *
     * @return List containing warnings that have been appealed
     * @throws SQLException if a database access error occurs
     */
    ArrayList<Warning> findAllAppealedWarnings() throws SQLException;
}
