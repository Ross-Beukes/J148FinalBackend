package com.j148.backend.warning.resource;

import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.service.WarningService;
import com.j148.backend.warning.service.WarningServiceImpl;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WarningResource handles HTTP requests related to warning operations.
 */
@Path("warning")
public class WarningResource {

    private final WarningService warningService = new WarningServiceImpl();
    private static final Logger LOG = Logger.getLogger(WarningResource.class.getName());

    /**
     * Endpoint to appeal a warning for a contractor.
     *
     * @param warning Warning object containing the appeal details and ID.
     * @return HTTP Response indicating the result of the appeal operation.
     */
    @POST
    @Path("appeal-warning/{warningId}")
    @Consumes(APPLICATION_JSON)
    public Response appealWarning(Warning warning,@PathParam("warningId") long warningId) {
        try {
            System.out.println("Appealing warning: " + warning);
            Warning appealedWarning = warningService.appealWarning(warning);
            if (appealedWarning != null) {
                return Response.ok(appealedWarning).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Warning or contractor not found").build();
            }
        } catch (IllegalArgumentException e) {
            LOG.log(Level.WARNING, "Invalid warning or contractor data", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid warning or contractor data").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An unexpected error occurred while appealing the warning", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error occurred").build();
        }
    }
}

