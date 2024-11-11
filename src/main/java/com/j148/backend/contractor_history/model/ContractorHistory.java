/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contractor_history.model;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.warning.model.Warning;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Tshireletso
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractorHistory  {
    
    private Contractor contractor;
    private List<Warning> warningHistory;
    private List<Hearing> hearingHistory;
    
    
    
}
