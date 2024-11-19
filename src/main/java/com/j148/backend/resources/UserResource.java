package com.j148.backend.resources;

import com.j148.backend.user.model.User;
import com.j148.backend.user.service.UserService;
import com.j148.backend.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
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



   private UserService userService = new UserServiceImpl();


    /**
     *This map is used to temporarily store the generated admin keys.
     */
    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());

    @GET
    public Response pingUserResource() {
        return Response.ok("Successfully pinged User Resource").build();
    }
    
    @POST
    @Consumes(APPLICATION_JSON)
    @Path("promote-staff")
    public Response promoteStaff(@Valid User user) {
        try {
            return Response.ok(this.userService.PromoteStaff(user)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to promote user details in the database");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "User object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to promote user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    } 

    

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("register")

    public Response registerUser(@Valid User user) {
        try {
            user.setRole(User.Role.APPLICANT);

            return Response.ok(this.userService.registerUser(user)).build();

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to add applicant to the database.  Check for duplicates");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e) {




            LOG.log(Level.SEVERE, e.getMessage());

            LOG.log(Level.SEVERE, "User object not complete.");
            System.out.println("sqlException : " + e.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to register user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }


    @POST
    @Consumes(APPLICATION_JSON)
    @Path("register-admin")
    public Response registerAdmin(@QueryParam("adminToken") String adminToken, User user) {
        try {
            user.setRole(User.Role.ADMIN);

            return Response.ok(this.userService.registerUser(user)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to add admin to the database.  Check for duplicates");
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, e.getMessage());
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

            return Response.ok(this.userService.registerUser(user)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to add instructor to the database.  Check for duplicates");
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to register user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }


    @POST
    @Consumes(APPLICATION_JSON)
    @Path("update-user")
    public Response updateUser(@Valid User user) {
        try {
            return Response.ok(this.userService.updateUser(user)).build();
        } catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to update user details in the database");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IllegalArgumentException e){
            LOG.log(Level.SEVERE, "User object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to update user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }


    @Produces(APPLICATION_JSON)
    @Path("find-user")
    public Response getUserFromEmail(@QueryParam("email") String email) {
        try {
            User found = userService.findUserByEmail(User.builder().email(email).build());

            return Response.ok(found).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to retrieve user from the database.");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {


            LOG.log(Level.SEVERE, "User object not complete.");
            LOG.log(Level.SEVERE, "Email parameter is not valid.");

            LOG.log(Level.SEVERE, "User object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to get user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }

    @GET
    @Path("get-user-by-id")
    public Response getUserById(@QueryParam("userId") long userId) {
        try {
            User user = User.builder().userId(userId).build();
            User found = userService.findUserById(user);
            return Response.ok(found).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to retrieve user from the database.");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "User object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to get user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }

    @POST
    @Consumes(APPLICATION_JSON)

    @Path("promote-user")
    public Response promoteUser(User user) {
        try {
//            User user = User.builder().idNumber(idNumber).build();
            return Response.ok(this.userService.promoteApplicant(user)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to update applicant's role in the database.");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "User object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to promote user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }

    @POST
    @Consumes(APPLICATION_JSON)

    @Path("login")
    public Response login(User user) {
        try {
            return Response.ok(this.userService.LogIn(user)).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "the user model passed to the server is invalid", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "unable to process user login", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }


}
