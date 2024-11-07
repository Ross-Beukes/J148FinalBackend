/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.warning.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.repo.WarningRepo;
import com.j148.backend.warning.repo.WarningRepoImpl;
import java.sql.SQLException;

/**
 *
 * @author glenl
 */
public class WarningServiceImpl implements WarningService {
    
    private WarningRepo warningRepo = new WarningRepoImpl();

    @Override
    public Warning lateComingWarning(Contractor contractor) throws SQLException, Exception {
        if (contractor != null) {
            return warningRepo.createLateWarning(contractor).orElseThrow(() -> new Exception("Warning could not be issued"));
        } else {
            throw new IllegalArgumentException("Contractor is null");
        }
    }

    @Override
    public Warning absentWarning(Contractor contractor) throws SQLException, Exception {
        if (contractor != null) {
            return warningRepo.createAbsentWarning(contractor).orElseThrow(() -> new Exception("Warning could not be issued"));
        } else {
            throw new IllegalArgumentException("Contractor is null");
        }
    }
    
}
