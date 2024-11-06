package com.j148.backend.resources;

import com.j148.backend.user.model.User;
import com.j148.backend.user.service.UserService;
import com.j148.backend.user.service.UserServiceImpl;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Controller for User management
 * Includes end point tests for all user management
 */
@Path("user")
public class UserResource {
    private UserService UserService = new UserServiceImpl();
    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());

    @GET
    public Response pingUserResource() {
        return Response.ok("Successfully pinged User Resource").build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("register-applicant")
    public Response registerApplicant(User user) {
        try {
            user.setRole(User.Role.APPLICANT);
            return Response.ok(this.UserService.registerUser(user)).build();
        } catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to add applicant to the database.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to register user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("register-admin")
    public Response registerAdmin(User user) {
        try {
            user.setRole(User.Role.ADMIN);
            return Response.ok(this.UserService.registerUser(user)).build();
        } catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to add admin to the database.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to register user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("register-instructor")
    public Response registerInstructor(User user) {
        try {
            user.setRole(User.Role.INSTRUCTOR);
            return Response.ok(this.UserService.registerUser(user)).build();
        } catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to add instructor to the database.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to register user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }
}
