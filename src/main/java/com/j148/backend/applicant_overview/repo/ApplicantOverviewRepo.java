/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.applicant_overview.repo;

import com.j148.backend.applicant_overview.model.ApplicantOverview;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author User
 */
public interface ApplicantOverviewRepo {
    
    List<ApplicantOverview> getAllApplicantOverview() throws SQLException;
    
}
