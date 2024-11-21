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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tshireletso
 */
public class ContractorHistoryServiceImpl implements ContractorHistoryService {
    
    private final HearingRepoImpl hearingRepoImpl = new HearingRepoImpl();
    private final WarningRepoImpl warningRepoImpl = new WarningRepoImpl(); 
    private static final Logger LOG = Logger.getLogger(ContractorHistoryServiceImpl.class.getName());
    
    

    @Override
    public ContractorHistory viewWarningAndHearingHistory(Contractor contractor) throws Exception {
        
        if(contractor != null && contractor.getContractorId()!= 0){
            
            
            try {
            List<Warning> warningHistory;
            List<Hearing> hearingHistory;
               
                if((hearingHistory = hearingRepoImpl.findContractorHearingHistory(contractor)) != null) { 
                
                warningHistory = warningRepoImpl.findAllActiveByContractor(contractor).orElseThrow(() -> new Exception(
                "Error collecting this contractors warning hsitory"));
  
                
                return ContractorHistory.builder()
                        .warningHistory(warningHistory)
                        .hearingHistory(hearingHistory)
                        .contractor(contractor)
                        .build();
                }else{
                    throw new RuntimeException("Apologies there was an issue recovering your disciplinary history");
                }
                
                
            }catch(SQLException e){
                LOG.log(Level.SEVERE, "Error collecting a contractor disciplinary history", e);
            }
            
            
        }
        else {
            throw new UserNotFoundException("User not found ,error generating contractor disciplinary history . Please try again later");
        }
        
        return null;
        
    }
    
}
