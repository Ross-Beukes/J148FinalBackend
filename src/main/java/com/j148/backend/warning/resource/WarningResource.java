package com.j148.backend.warning.resource;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.service.WarningService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WarningResource handles HTTP requests related to warning operations.
 */
@RequestScoped
@Path("warning")
public class WarningResource {

    @Inject
    private WarningService warningService;
    private static final Logger LOG = Logger.getLogger(WarningResource.class.getName());

    @GET
    @Path("/get-appealed-warning")
    public Response getAllAppealedWarning(){
    
        try{
            return Response.ok(warningService.findAllAppealedWarnings()).build();
        }catch(SQLException e){
        LOG.log(Level.SEVERE, "Error collecting all appealed warnings from database", e);
        return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
        catch(Exception e){
        LOG.log(Level.SEVERE, "There was an unexpected error while getting appealed warnings", e);
        return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        
        }
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
    @Consumes(APPLICATION_JSON)
    @Path("/appeal-warning")
    public Response appealWarning(Warning warning) {

        if (warning != null) {
            Contractor contractor = warning.getContractor();
            try {
                return Response.ok(warningService.appealWarning(warning, contractor)).build();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "There was an unexpected error while getting appealed warnings", e);
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            }
        } else {
            LOG.log(Level.SEVERE, "There is no such warning , Object is null", warning);
            return Response.status(Response.Status.NOT_FOUND).entity(warning).build();
        }

    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("/update-warning")
    public Response updateWarning(Warning warning) {

        if (warning != null) {

            try {
                return Response.ok(warningService.updateState(warning)).build();

            } catch (SQLException e) {
                LOG.log(Level.SEVERE, "Error updating warning state in the database", e);
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "There was an unexpected error while getting appealed warnings", e);
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            }
        } else {
            LOG.log(Level.SEVERE, "There is no such warning , Object coming to the resource layer is null", warning);
            return Response.status(Response.Status.NOT_FOUND).entity(warning).build();
        }

    }

}

