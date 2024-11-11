package com.j148.backend.resources;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.hearing.service.HearingService;
import com.j148.backend.hearing.service.HearingServiceImpl;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("user")
public class RescheduleHearingResource {
    private HearingService hearingService = new HearingServiceImpl();
    private static final Logger LOG = Logger.getLogger(RescheduleHearingResource.class.getName());

    @GET
    public Response pingRescheduleHearingResource(){
        return Response.ok("Successfully pinged Hearing resource").build();
    }

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
}
