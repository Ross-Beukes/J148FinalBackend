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
    private UserService userService = new UserServiceImpl();
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
    @Path("generate-verification-token")
    public Response generateVerificationToken(@QueryParam("applicantEmail")String applicantEmail){
        String token = userService.generateVerificationToken();
        verificationTokens.put(token, applicantEmail);

        String subject = "Your Email Verification Token";

        String messageBody = "Hello,\n\nHere is your email verification token: \n\n" +
                "Token: " + token + "\n\n" +
                "Use this token to complete your registration.\n\n" +
                "Best regards,\nYour Application Team";

        try{
            EmailService.sendEmail(applicantEmail, subject, messageBody);
            System.out.println("Token sent to email: " + applicantEmail);
        } catch (MessagingException e) {
            LOG.log(Level.SEVERE, "Unable to send an email to this email address " + applicantEmail);
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
        return Response.ok(token).build();
    }

    @POST
    @Path("generate-admin-token")
    public Response generateTokenForAdmin(@QueryParam("adminEmail")String adminEmail){
        String token = userService.generateAdminToken();
        verificationTokens.put(token, adminEmail);

        String subject = "Your Admin Registration Token";

        String messageBody = "Hello,\n\nHere is your registration token: \n\n" +
                "Token: " + token + "\n\n" +
                "Use this token to complete your registration.\n\n" +
                "Best regards,\nYour Application Team";

        try{
            EmailService.sendEmail(adminEmail, subject, messageBody);
            System.out.println("Token sent to email: " + adminEmail);
        } catch (MessagingException e) {
            LOG.log(Level.SEVERE, "Unable to send an email to this email address " + adminEmail);
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
        return Response.ok(token).build();
    }

    @POST
    @Path("generate-instructor-token")
    public Response generateTokenForInstructor(@QueryParam("instructorEmail")String instructorEmail){
        String token = userService.generateInstructorToken();
        verificationTokens.put(token, instructorEmail);

        String subject = "Your Instructor Registration Token";

        String messageBody = "Hello,\n\nHere is your registration token: \n\n" +
                "Token: " + token + "\n\n" +
                "Use this token to complete your registration.\n\n" +
                "Best regards,\nYour Application Team";

        try{
            EmailService.sendEmail(instructorEmail, subject, messageBody);
            System.out.println("Token sent to email: " + instructorEmail);
        } catch (MessagingException e) {
            LOG.log(Level.SEVERE, "Unable to send an email to this email address " + instructorEmail);
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
        return Response.ok(token).build();
    }


    @POST
    @Consumes(APPLICATION_JSON)
    @Path("register-applicant")
    public Response registerApplicant(@QueryParam("applicantToken") String applicantToken, User user) {
        try {
            String email = verificationTokens.get(applicantToken);
            if (applicantToken == null || email == null || !email.equals(user.getEmail())) {
                return Response.status(Response.Status.FORBIDDEN).entity("Invalid verificationToken token or email mismatch.").build();
            }


            user.setRole(User.Role.APPLICANT);

            verificationTokens.remove(applicantToken);

            return Response.ok(this.userService.registerUser(user)).build();
        } catch (SQLException e){
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
    @Path("register-admin")
    public Response registerAdmin(@QueryParam("adminToken") String adminToken, User user) {
        try {
            String email = verificationTokens.get(adminToken);
            if (adminToken == null || email == null || !email.equals(user.getEmail())) {
                return Response.status(Response.Status.FORBIDDEN).entity("Invalid verificationToken token or email mismatch.").build();
            }

            user.setRole(User.Role.ADMIN);

            verificationTokens.remove(adminToken);

            return Response.ok(this.userService.registerUser(user)).build();
        } catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to add admin to the database.  Check for duplicates");
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
    @Path("register-instructor")
    public Response registerInstructor(@QueryParam("instructorToken") String instructorToken, User user) {
        try {
            String email = verificationTokens.get(instructorToken);
            if (instructorToken == null || email == null || !email.equals(user.getEmail())) {
                return Response.status(Response.Status.FORBIDDEN).entity("Invalid verificationToken token or email mismatch.").build();
            }

            user.setRole(User.Role.INSTRUCTOR);

            verificationTokens.remove(instructorToken);
            return Response.ok(this.userService.registerUser(user)).build();
        } catch (SQLException e){
            LOG.log(Level.SEVERE, "Unable to add instructor to the database.  Check for duplicates");
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
    @Path("update-user")
    public Response updateUser(User user) {
        try {
            return Response.ok(this.userService.updateUser(user)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to update user details in the database");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {
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
            User found = userService.findUserByEmail(user);
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

    @GET
    @Path("get-user-by-id")
    public Response getUserById(@QueryParam("userId")long userId){
        try{
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
    public Response promoteUser(@QueryParam("idNumber")String idNumber){
        try{
            User user = User.builder().idNumber(idNumber).build();
            return Response.ok(this.userService.promoteApplicant(user)).build();
        }catch (SQLException e){
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
