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

    /**
     * Issues a warning to a contractor for late arrival.
     *
     * @param contractor The Contractor object to issue the warning to.
     * @return A Warning object representing the issued warning for late
     * arrival.
     * @throws SQLException If a database access error occurs.
     * @throws Exception If the warning could not be created or the contractor
     * is not found.
     * @throws IllegalArgumentException If the contractor is null.
     */
    Warning lateComingWarning(Contractor contractor) throws SQLException, Exception;

    /**
     * Issues a warning to a contractor for being absent.
     *
     * @param contractor The Contractor object to issue the warning to.
     * @return A Warning object representing the issued warning for absence.
     * @throws SQLException If a database access error occurs.
     * @throws Exception If the warning could not be created or the contractor
     * is not found.
     * @throws IllegalArgumentException If the contractor is null.
     */
    Warning absentWarning(Contractor contractor) throws SQLException, Exception;

}
