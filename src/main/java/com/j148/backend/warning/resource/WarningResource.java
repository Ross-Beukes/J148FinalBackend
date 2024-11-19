package com.j148.backend.warning.resource;

import com.j148.backend.contractor.model.Contractor;
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
     * @param contractor Contractor object containing the contractor's ID.
     * @return HTTP Response indicating the result of the appeal operation.
     */

}

