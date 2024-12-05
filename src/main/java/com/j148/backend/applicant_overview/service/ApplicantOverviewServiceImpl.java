/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.applicant_overview.service;

import com.j148.backend.Exceptions.ApplicantNotFoundException;
import com.j148.backend.Exceptions.ApplicantOverviewNotFoundException;
import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.applicant_overview.model.ApplicantOverview;
import com.j148.backend.applicant_overview.repo.ApplicantOverviewRepo;
import jakarta.inject.Inject;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class ApplicantOverviewServiceImpl implements ApplicantOverviewService{

    @Inject
    private ApplicantOverviewRepo applicantOverviewRepo; 
    
    @Override
    public List<ApplicantOverview> getAllApplicantOverview() throws SQLException, UserNotFoundException, ApplicantOverviewNotFoundException {
        List<ApplicantOverview> applicantOverviews = applicantOverviewRepo.getAllApplicantOverview();

        if (applicantOverviews.isEmpty()) {
            return List.of();//return empty list
        }

        for (ApplicantOverview ao : applicantOverviews) {
            validateOverviews(ao);
        }
        return applicantOverviews;
    }
    public void validateOverviews (ApplicantOverview applicantOverview) throws ApplicantOverviewNotFoundException, UserNotFoundException {
        if (applicantOverview == null) {
            throw new ApplicantOverviewNotFoundException("Applicant Overview Object is equal to null");
        }
        if (applicantOverview.getDocuments().getUser() == null || applicantOverview.getDocuments().getUser().getUserId() == null || applicantOverview.getDocuments().getUser().getUserId() == 0) {
            throw new UserNotFoundException("User object is equal to null or the userId is 0");
        }
    }
}
