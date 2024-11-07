/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.warning.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.warning.model.Warning;
import java.sql.SQLException;

/**
 *
 * @author glenl
 */
public interface WarningService {
    
    Warning lateComingWarning(Contractor contractor) throws SQLException, Exception;
    
    Warning absentWarning(Contractor contractor) throws SQLException, Exception;
    
}
