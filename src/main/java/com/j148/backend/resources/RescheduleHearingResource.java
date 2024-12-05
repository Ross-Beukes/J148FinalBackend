package com.j148.backend.resources;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.hearing.service.HearingService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.List;
@RequestScoped
@Path("hearing")
public class RescheduleHearingResource {
    @Inject
    private HearingService hearingService;
    private static final Logger LOG = Logger.getLogger(RescheduleHearingResource.class.getName());

//    @GET
//    public Response pingRescheduleHearingResource(){
//        return Response.ok("Successfully pinged Hearing resource").build();
//    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("reschedule-hearing/{contractorId}")
    public Response rescheduleHearing(Hearing hearing, @PathParam("contractorId")long contractorId){
        try{
            Contractor contractor = Contractor.builder().contractorId(contractorId).build();
            Hearing rescheduled = hearingService.rescheduleHearing(hearing, contractor);
            return Response.ok(rescheduled).build();
        }catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to reschedule hearing in the database");
            return Response.status(Response.Status.CONFLICT).build();
        }catch (IllegalArgumentException e){
            LOG.log(Level.SEVERE, "Hearing or contractor object is incomplete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }catch (Exception e){
            LOG.log(Level.SEVERE, "Unable to reschedule hearing");
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }
    
    @GET
    @Produces(APPLICATION_JSON)
    @Path("all-hearings")
    public Response getAllHearings() {
        try {
            List<Hearing> hearings = (List<Hearing>) hearingService.getAllHearings();
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
