/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contractor_history.service;

import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor_history.model.ContractorHistory;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.hearing.repo.HearingRepoImpl;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.repo.WarningRepoImpl;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tshireletso
 */
public class ContractorHistoryServiceImpl implements ContractorHistoryService {
    
    private HearingRepoImpl hri = new HearingRepoImpl();
    private WarningRepoImpl wri = new WarningRepoImpl();        
    

    @Override
    public ContractorHistory viewWarningAndHearingHistory(Contractor contractor) throws Exception {
        
        if(contractor != null && contractor.getContractorId()!= 0){
            
            List<Warning> warningHistory = new ArrayList<>();
            List<Hearing> hearingHistory = new ArrayList<>();
            
            try {
                hearingHistory = hri.findContractorHearingHistory(contractor);
                warningHistory = wri.findAllActiveByContractor(contractor).get();
                
                ContractorHistory c = ContractorHistory.builder()
                        .warningHistory(warningHistory)
                        .hearingHistory(hearingHistory)
                        .contractor(contractor)
                        .build();
                
               
                return c;
                
                
            } catch (SQLException ex) {
               System.out.println("Error while view a contractors disciplinary history");
            }
            
            
        }
        else {
            throw new UserNotFoundException("User not found ,error generating contractor disciplinary history . Please try again later");
        }
        
        return null;
        
    }
    
}
