package com.j148.backend.warning.resource;

import com.j148.backend.warning.service.WarningService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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

}

