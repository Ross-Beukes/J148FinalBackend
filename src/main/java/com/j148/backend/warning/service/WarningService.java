/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.warning.service;

import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.WarningNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.warning.model.Warning;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author arshr
 */
public interface WarningService {
    Optional<Warning> save(Warning warning) throws SQLException, WarningNotFoundException, ContractorNotFoundException;
    
    Optional<Warning> findById(Warning warning) throws SQLException;
    
    Optional<List<Warning>> findByContractor(Contractor contractor) throws SQLException;
    
    Optional<List<Warning>> findAllActiveByContractor(Contractor contractor) throws SQLException;
    
    Optional<List<Warning>> findAppealedByContractor(Contractor contractor) throws SQLException;
    
    Optional<Warning> updateState(Warning warning) throws SQLException;
    
    Optional<Warning> createLateWarning(Contractor contractor) throws SQLException;
    
    Optional<List<Warning>> findWarningsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
    
    Optional<List<Warning>> findFinalWarningsByContractor(Contractor contractor) throws SQLException;
    
    Optional<Long> countActiveWarningsByContractor(Contractor contractor) throws SQLException;
    
    Optional<List<Warning>> findWarningsByReason(Warning warning) throws SQLException;
    
    Optional<Boolean> existsByContractorAndDateIssue(Contractor contractor, LocalDateTime dateIssue) throws SQLException;
    
    
}
