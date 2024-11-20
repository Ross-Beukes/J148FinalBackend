/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.resources;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.service.WarningService;
import com.j148.backend.warning.service.WarningServiceImpl;
import jakarta.ws.rs.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author glenl
 */
@Path("warning")
public class WarningResource {

    private WarningService warningService = new WarningServiceImpl();
    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());

    @GET
    public Response pingUserResource() {
        return Response.ok("Successfully pinged warning Resource").build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("appeal-warning/{contractorId}")
    public Response appealWarning(Warning warning, @PathParam("contractorId") long contractorId) {
        try {
            Contractor contractor = Contractor.builder().contractorId(contractorId).build();
            
            return Response.ok(this.warningService.appealWarning(warning, contractor)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to add warning to the database.  Check for duplicates");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "Warning object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to add warning", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }
    
    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Path("GetContractorWarnings")
    public Response retrieveContractorWarning(Contractor contractor) {
        try {
            return Response.ok(this.warningService.findAllActiveByContractor(contractor)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to get warnings from the database.");
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "Contractor object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to get Warning", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }
}
