/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.resources;

import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.hearing.service.HearingService;
import com.j148.backend.user.model.User;
import static com.mysql.cj.conf.PropertyKey.logger;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author glenl
 */
@RequestScoped
@Path("hearing")
public class HearingResource {
    
    @Inject
    private HearingService hearingService;
    
    private static final Logger LOG = Logger.getLogger(HearingResource.class.getName());
    
    @GET
    public Response pingHearingResource() {
        return Response.ok("Successfully pinged Hearing Resource").build();
    }
    
    @GET
    @Produces(APPLICATION_JSON)
    @Path("all-hearings")
    public Response getAllHearings() {
        try {
            List<Hearing> hearings = hearingService.getAllHearings();
            return Response.ok(hearings).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error retrieving contract periods", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving all hearings").build();
        }
    }
    
    @POST
    @Consumes(APPLICATION_JSON)
    @Path("update-hearing")
    public Response updateHearing(Hearing hearing) {
        try {
            return Response.ok(this.hearingService.updateHearing(hearing)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to update hearing details in the database");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "Hearing object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to update hearing", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }
}
