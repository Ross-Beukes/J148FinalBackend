/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.applicant_overview.service;

import com.j148.backend.applicant_overview.model.ApplicantOverview;
import java.sql.SQLException;
import java.util.List;

import com.j148.backend.Exceptions.ApplicantNotFoundException;
import com.j148.backend.Exceptions.ApplicantOverviewNotFoundException;
import com.j148.backend.Exceptions.UserNotFoundException;

/**
 *
 * @author Yusuf
 */
public interface ApplicantOverviewService {
    List<ApplicantOverview> getAllApplicantOverview() throws SQLException, UserNotFoundException, ApplicantOverviewNotFoundException;
}
