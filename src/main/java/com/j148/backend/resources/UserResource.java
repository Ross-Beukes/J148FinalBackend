package com.j148.backend.resources;

import com.j148.backend.user.EmailService;
import com.j148.backend.user.model.User;
import com.j148.backend.user.service.UserService;
import com.j148.backend.user.service.UserServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
    /**
     *This map is used to temporarily store the generated admin keys.
     */
    private static final Map<String, String> adminTokens = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());

    @GET
    public Response pingUserResource() {
        return Response.ok("Successfully pinged User Resource").build();
    }

    @POST
    @Path("generate-admin-token")
    public Response generateTokenForAdmin(@QueryParam("adminEmail")String adminEmail){
        String token = UserService.generateAdminToken();
        adminTokens.put(token, adminEmail);

        try{
            EmailService.sendEmail(adminEmail, token);
            System.out.println("Token sent to email: " + adminEmail);
        }catch (MessagingException e){
            LOG.log(Level.SEVERE, "Unable to send an email to this email address " + adminEmail);
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
        return Response.ok(token).build();
    }


    @POST
    @Consumes(APPLICATION_JSON)
    @Path("register-applicant")
    public Response registerApplicant(@Valid User user) {
        try {
            user.setRole(User.Role.APPLICANT);
            System.out.println(user.toString());
            return Response.ok(this.UserService.registerUser(user)).build();
        } catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to add applicant to the database.  Check for duplicates");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e){
            LOG.log(Level.SEVERE, "User object not complete.");
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
            if (adminToken == null || !adminTokens.containsKey(adminToken)){
                return Response.status(Response.Status.FORBIDDEN).entity("Invalid admin token.").build();
            }

            user.setRole(User.Role.ADMIN);

            adminTokens.remove(adminToken);

            return Response.ok(this.UserService.registerUser(user)).build();
        } catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to add admin to the database.  Check for duplicates");
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e){
            LOG.log(Level.SEVERE, "User object not complete.");
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
            LOG.log(Level.SEVERE, "Unable to add instructor to the database.  Check for duplicates");
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e){
            LOG.log(Level.SEVERE, "User object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to register user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("login")
    public Response login(@QueryParam("email") String email, @QueryParam("password") String password){
        try{
            User user = User.builder().email(email).password(password).build();
            User found = UserService.LogIn(user);
            if (found != null && found.getPassword().equals(password)) {
                return Response.ok(found).build();
            }else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid email or password").build();
            }
        }catch(IllegalArgumentException e){
            LOG.log(Level.SEVERE, "The user model passed to the server is invalid", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }catch(Exception e){
            LOG.log(Level.SEVERE, "Unable to process user login", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }


    @POST
    @Consumes(APPLICATION_JSON)
    @Path("update-user")
    public Response updateUser(User user) {
        try {
            return Response.ok(this.UserService.updateUser(user)).build();
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

    @GET
    @Path("get-user")
    public Response getUserFromEmail(@QueryParam("userEmail")String userEmail) {
        try {
            User user = User.builder().email(userEmail).build();
            User found = UserService.findUserByEmail(user);
            return Response.ok(found).build();
        } catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to retrieve user from the database.");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IllegalArgumentException e){
            LOG.log(Level.SEVERE, "User object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to get user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }

    @GET
    @Path("get-user-by-id")
    public Response getUserById(@QueryParam("userId")long userId){
        try{
            User user = User.builder().userId(userId).build();
            User found = UserService.findUserById(user);
            return Response.ok(found).build();
        }catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to retrieve user from the database.");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IllegalArgumentException e){
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
    public Response promoteUser(User user){
        try{
            return Response.ok(this.UserService.promoteApplicant(user)).build();
        }catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to update applicant's role in the database.");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e){
            LOG.log(Level.SEVERE, "User object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to promote user", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }
}
