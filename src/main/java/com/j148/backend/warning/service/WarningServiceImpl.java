/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.warning.service;

import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.WarningNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.repo.WarningRepo;
import com.j148.backend.warning.repo.WarningRepoImpl;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author arshr
 */
public class WarningServiceImpl implements WarningService {

    private WarningRepo warningRepo = new WarningRepoImpl();

    @Override
    public Optional<Warning> save(Warning warning) throws SQLException, WarningNotFoundException, ContractorNotFoundException {
        // Check if the warning object is null and throw exception if it is
        if (warning == null) {
            throw new WarningNotFoundException("Warning returned a null");
        } else if (warning.getContractor() == null || warning.getContractor().getContractorId() == 0) {
            throw new ContractorNotFoundException("Warning does not contain contractor");
        }else if(warning.getReason() == null){
            throw new WarningNotFoundException("Reason not found");
        }

        return Optional.empty();
    }

    @Override
    public Optional<Warning> findById(Warning warning) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<Warning> updateState(Warning warning) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<List<Warning>> findByContractor(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<List<Warning>> findAllActiveByContractor(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<List<Warning>> findAppealedByContractor(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<Warning> createLateWarning(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<List<Warning>> findWarningsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<List<Warning>> findFinalWarningsByContractor(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<Long> countActiveWarningsByContractor(Contractor contractor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<Boolean> existsByContractorAndDateIssue(Contractor contractor, LocalDateTime dateIssue) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<List<Warning>> findWarningsByReason(Warning warning) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
