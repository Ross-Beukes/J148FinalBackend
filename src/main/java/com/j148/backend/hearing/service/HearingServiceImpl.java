/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.hearing.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.hearing.repo.HearingRepoImpl;
import java.sql.SQLException;
import com.j148.backend.warning.repo.WarningRepoImpl;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Tshireletso
 */
public class HearingServiceImpl implements HearingService {
    
    @Override
    public LocalDateTime scheduleHearing() throws Exception{
        // get one week to the current date and time
        LocalDateTime hearingDate = LocalDateTime.now().plusWeeks(1);
    
       return hearingDate;
    }
    
    @Override
    public Hearing IssueHearing(Contractor contractor) throws Exception{
        
        if(contractor != null){
            int hearingCount = 0; 
            long warningCount = 0l;
            HearingRepoImpl hri = new HearingRepoImpl();
           
            //Obtain count based on a Contractors amount of hearings already
            try {
                hearingCount = hri.findContractorHearingHistory(contractor).size();
            } catch (SQLException ex) {
                Logger.getLogger(HearingServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            WarningRepoImpl wri = new WarningRepoImpl();
            //Obtain count based on a Contractors amount of active warnings
            try {
                warningCount = wri.countActiveWarningsByContractor(contractor).get() ;
            } catch (SQLException ex) {
                Logger.getLogger(HearingServiceImpl.class.getName()).log(Level.SEVERE, "Error while viewing warning history", ex);
            }
            
            
            if(warningCount % 3 == 0 && warningCount > 0){
                
                if(hearingCount * 3 != warningCount){
                    Hearing hearing = Hearing.builder()
                            .scheduleDate(scheduleHearing())
                            .hearingsId(0L)
                            .contractor(contractor)
                            .reason("Contractor has received three or more warnings for being late or absent")
                            .outcome(Hearing.Outcome.NULL)
                            .build();
                     return hearing;
                }
                
             }
            }
            return null;
        }
        
     
        
    }
    
  
    
    

