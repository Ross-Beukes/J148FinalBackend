package com.j148.backend.warning.service;

import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.WarningNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.repo.WarningRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author glenl
 */
@ApplicationScoped
public class WarningServiceImpl implements WarningService {

    @Inject
    private WarningRepo warningRepo;
    @Inject
    private ContractorRepo contractorRepo;

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public Warning lateComingWarning(Contractor contractor) throws SQLException, Exception {
        if (contractor != null) {
            if (contractor.getContractorId() != null) {
                return warningRepo.createLateWarning(contractor).orElseThrow(() -> new RuntimeException("Warning could not be issued"));
            } else {
                throw new IllegalArgumentException("Contract ID is null");
            }
        } else {
            throw new IllegalArgumentException("Contractor is null");
        }
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public Warning absentWarning(Contractor contractor) throws SQLException, Exception {
        if (contractor != null) {
            if (contractor.getContractorId() != null) {
                return warningRepo.createAbsentWarning(contractor).orElseThrow(() -> new RuntimeException("Warning could not be issued"));
            } else {
                throw new IllegalArgumentException("Contract ID is null");
            }
        } else {
            throw new IllegalArgumentException("Contractor is null");
        }
    }

    @Override
    public Warning save(Warning warning) throws SQLException, WarningNotFoundException, ContractorNotFoundException {
        return null;
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public Warning appealWarning(Warning warning, Contractor contractor) throws Exception {

        if (warning == null) {
            throw new IllegalArgumentException("Warning is null");
        }
        if (contractor == null) {
            throw new IllegalArgumentException("Contractor is null");
        }

        if (warning.getWarningId() == null) {
            throw new IllegalArgumentException("Warning id is null");
        }

        if (contractor.getContractorId() == null) {
            throw new IllegalArgumentException("Contractor id is null");
        }

        if (contractorRepo.findById(contractor).isEmpty()) {
            throw new IllegalArgumentException("Could not find contractor");
        }

        if (warningRepo.findById(warning).isEmpty()) {
            throw new IllegalArgumentException("Could not find warning");
        }

        return warningRepo.updateState(warning)
                .orElseThrow(() -> new RuntimeException("Failed to appeal warning"));

    }
    @Override
    public Warning findById(Warning warning) throws SQLException {
        return null;
    }

    @Override
    public List<Warning> findByContractor(Contractor contractor) throws SQLException {
        return null;
    }

    @Override
    public List<Warning> findAllActiveByContractor(Contractor contractor) throws SQLException, Exception {
        if (contractor != null) {
             System.out.println(warningRepo.findAllActiveByContractor(contractor).orElseThrow(() -> new Exception("No Warnings found.")));
             return warningRepo.findAllActiveByContractor(contractor).orElseThrow(() -> new Exception("No Warnings found."));
        }
        return null;
    }

    @Override
    public List<Warning> findAppealedByContractor(Contractor contractor) throws SQLException {
        return null;
    }

    @Override
    public Warning updateState(Warning warning) throws SQLException {
        warning.setState(Warning.WarningState.APPEALED);
        return warning;
    }

    @Override
    public Warning createLateWarning(Contractor contractor) throws SQLException {
        return null;
    }

    @Override
    public List<Warning> findWarningsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        return null;
    }

    @Override
    public List<Warning> findFinalWarningsByContractor(Contractor contractor) throws SQLException {
        return null;
    }

    @Override
    public Optional<Long> countActiveWarningsByContractor(Contractor contractor) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<Warning> findWarningsByReason(Warning warning) throws SQLException {
        return null;
    }

    @Override
    public Boolean existsByContractorAndDateIssue(Contractor contractor, LocalDateTime dateIssue) throws SQLException {
        return null;
    }

    
    @Override
    public ArrayList<Warning> findAllAppealedWarnings() throws SQLException{
        
        return warningRepo.findAllAppealedWarnings();
    
    }
}

