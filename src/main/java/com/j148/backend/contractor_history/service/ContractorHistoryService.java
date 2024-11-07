/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contractor_history.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor_history.model.ContractorHistory;

/**
 *
 * @author Tshireletso
 */
public interface ContractorHistoryService {
    
  /**
   * This method returns a contractor history object which contains a list of
   * both a specific contractors warnings and hearings and returns it.
   * 
   * @param Contractor
   * @return ContractorHistory
   */   
   public ContractorHistory viewWarningAndHearingHistory(Contractor contractor) throws Exception;
   
   
    
}
