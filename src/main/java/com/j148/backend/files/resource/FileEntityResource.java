/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.files.resource;

import com.j148.backend.files.model.FileEntity;
import com.j148.backend.files.service.FileEntityService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author ledi
 */

@RequestScoped
@Path("files_entity")
@Produces(APPLICATION_JSON)
public class FileEntityResource {
    @Inject
    private FileEntityService FileEntityService;


    /**
     * This map is used to temporarily store the generated admin keys.
     */
    private static final Logger LOG = Logger.getLogger(FileEntityResource.class.getName());

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("verify")
    public Response verifyDocs(FileEntity fileEntity) {
        try {
            return Response.ok(this.FileEntityService.fileVerification(fileEntity)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to verify file in the database.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "File object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to verify file", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }

    @GET
    @Path("files_and_users")
    public Response getAllFilesWithUsers() {
        try {
            return Response.ok(this.FileEntityService.retrieveFilesWithUsers()).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        }
    }
}
