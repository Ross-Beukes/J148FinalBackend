package com.j148.backend.resources;

import com.j148.backend.user.EmailService;
import com.j148.backend.user.model.User;
import com.j148.backend.user.service.UserService;
import com.j148.backend.user.service.UserServiceImpl;
import jakarta.mail.MessagingException;
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
    private static final Map<String, String> verificationTokens = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());

    @GET
    public Response pingUserResource() {
        return Response.ok("Successfully pinged User Resource").build();
    }

    @POST
    @Path("generate-token")
    public Response generateVerificationToken(@QueryParam("email")String email, @QueryParam("role") String role){
        String token;
        String subject;

        switch (role.toLowerCase()) {
            case "applicant":
                token = UserService.generateVerificationToken();
                subject = "Your Email Verification Token";
                break;
            case "admin":
                token = UserService.generateAdminToken();
                subject = "Your Admin Registration Token";
                break;
            case "instructor":
                token = UserService.generateInstructorToken();
                subject = "Your Instructor Registration Token";
                break;
            default:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid role specified.").build();
        }
        verificationTokens.put(token, email);

        String messageBody = "Hello,\n\nHere is your email verification token: \n\n" +
                "Token: " + token + "\n\n" +
                "Use this token to complete your registration.\n\n" +
                "Best regards,\nYour Application Team";

        try{
            EmailService.sendEmail(email, subject, messageBody);
            System.out.println("Token sent to email: " + email);
        } catch (MessagingException e) {
            LOG.log(Level.SEVERE, "Unable to send an email to this email address " + email);
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
        return Response.ok(token).build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("register")
    public Response registerUser(@QueryParam("token") String token, User user) {
        try{
            if (token == null || token.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Token is required.").build();
            }

            char firstLetter = token.charAt(0);
            String email = verificationTokens.get(token);
            if (email == null || !email.equals(user.getEmail())) {
                return Response.status(Response.Status.FORBIDDEN).entity("Invalid verification token or email mismatch.").build();
            }


            switch (firstLetter) {
                case 'A': // Admin role
                    user.setRole(User.Role.ADMIN);
                    break;
                case 'I': // Instructor role
                    user.setRole(User.Role.INSTRUCTOR);
                    break;
                case 'V': // Applicant role
                    user.setRole(User.Role.APPLICANT);
                    break;
                default:
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid token format.").build();
            }

            verificationTokens.remove(token);

            return Response.ok(this.UserService.registerUser(user)).build();

        }catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to add applicant to the database.  Check for duplicates");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e){
            LOG.log(Level.SEVERE, e.getMessage());
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
    @Path("find-user")
    public Response getUserFromEmail(@QueryParam("email")String email) {
        try {
            User user = User.builder().email(email).build();
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
    @Path("find-user-by-id")
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
    public Response promoteUser(@QueryParam("idNumber")String idNumber){
        try{
            User user = User.builder().idNumber(idNumber).build();
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
