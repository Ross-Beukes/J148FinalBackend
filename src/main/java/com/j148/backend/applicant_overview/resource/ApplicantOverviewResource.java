/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.applicant_overview.resource;

import com.j148.backend.Exceptions.ApplicantNotFoundException;
import com.j148.backend.Exceptions.ApplicantOverviewNotFoundException;
import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.applicant_overview.service.ApplicantOverviewService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */

@RequestScoped
@Path("applicant-overview")
public class ApplicantOverviewResource {

    @Inject
    private ApplicantOverviewService applicantOverviewService;
    private static final Logger LOG = Logger.getLogger(ApplicantOverviewResource.class.getName());

    @GET
    @Path("get-all-applicant-overview")
    public Response getAllApplicantOverview() {
        try {
            System.out.println(this.applicantOverviewService.getAllApplicantOverview().toString());
            return Response.ok(this.applicantOverviewService.getAllApplicantOverview()).build();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "There was an error retrieving the list from the database", ex);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (UserNotFoundException ex) {
            LOG.log(Level.SEVERE, "User or User ID was null", ex);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (ApplicantOverviewNotFoundException ex) {
            LOG.log(Level.SEVERE, "Applicant Overview was null", ex);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        }

    }

}
