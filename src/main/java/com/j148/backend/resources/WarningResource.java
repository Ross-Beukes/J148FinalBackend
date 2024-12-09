/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.resources;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.service.WarningService;
import com.j148.backend.warning.service.WarningServiceImpl;
import jakarta.inject.Inject;
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
@Path("warninsg")
public class WarningResource {

    @Inject
    private WarningService warningService;
    //private static final Logger LOG = Logger.getLogger(UserResource.class.getName());

    @GET
    public Response pingUserResource() {
        return Response.ok("Successfully pinged warning Resource").build();
    }


}
