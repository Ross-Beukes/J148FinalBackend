package com.j148.backend.warning.service;

import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.WarningNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.contractor.repo.ContractorRepoImpl;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.repo.WarningRepo;
import com.j148.backend.warning.repo.WarningRepoImpl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class WarningServiceImpl implements WarningService{

    private final WarningRepo warningRepo = new WarningRepoImpl();
    private final ContractorRepo contractorRepo = new ContractorRepoImpl();
    @Override
    public Warning appealWarning(Warning warning, Contractor contractor) throws Exception {

        if (warning == null){
            throw new IllegalArgumentException("Warning is null");
        }
        if (contractor == null){
            throw new IllegalArgumentException("Contractor is null");
        }

        if (warning.getWarningId() == null ){
            throw new IllegalArgumentException("Warning id is null");
        }

        if (contractor.getContractorId() == null){
            throw new IllegalArgumentException("Contractor id is null");
        }

        if (contractorRepo.findById(contractor.getContractorId()).isEmpty()){
            throw new IllegalArgumentException("Could not find contractor");
        }

        if (warningRepo.findById(warning).isEmpty()){
            throw new IllegalArgumentException("Could not find warning");
        }

        return warningRepo.updateState(warning)
                .orElseThrow(() -> new Exception("Failed to appeal warning"));

    }

    @Override
    public Warning save(Warning warning) throws SQLException, WarningNotFoundException, ContractorNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Warning findById(Warning warning) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Warning> findByContractor(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");  }

    @Override
    public List<Warning> findAllActiveByContractor(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Warning> findAppealedByContractor(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Warning updateState(Warning warning) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Warning createLateWarning(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Warning> findWarningsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Warning> findFinalWarningsByContractor(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<Long> countActiveWarningsByContractor(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Warning> findWarningsByReason(Warning warning) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean existsByContractorAndDateIssue(Contractor contractor, LocalDateTime dateIssue) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
