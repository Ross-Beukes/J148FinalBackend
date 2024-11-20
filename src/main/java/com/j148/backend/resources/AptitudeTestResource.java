/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.resources;

import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.aptitude_test.service.AptitudeTestService;
import com.j148.backend.aptitude_test.service.AptitudeTestServiceImpl;
import com.j148.backend.user.model.User;
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
@Path("aptitude-test")
public class AptitudeTestResource {
    
    private AptitudeTestService aptitudeTestService = new AptitudeTestServiceImpl();
    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());
    
    @GET
    public Response pingUserResource() {
        return Response.ok("Successfully pinged aptitude test Resource").build();
    }
    
    @POST
    @Consumes(APPLICATION_JSON)
    @Path("schedule_aptitude_test")
    public Response scheduleAptitudeTest(AptitudeTest aptitudeTest, @QueryParam("userId") long userId) {
        try {
            User user = User.builder().userId(userId).build();
            System.out.println(userId);
            return Response.ok(this.aptitudeTestService.scheduleTest(aptitudeTest, user)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to add aptitude test to the database.  Check for duplicates");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "Aptitude Test object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to add Aptitude Test", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }
    
    @POST
    @Consumes(APPLICATION_JSON)
    @Path("reschedule_aptitude_test")
    public Response rescheduleAptitudeTest(AptitudeTest aptitudeTest, @QueryParam("userId") long userId) {
        try {
            User user = User.builder().userId(userId).build();
            System.out.println(userId);
            return Response.ok(this.aptitudeTestService.rescheduleTest(aptitudeTest, user)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Unable to change aptitude test in the database.");
            System.out.println("sqlException : " + e.getMessage());
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "Aptitude Test object not complete.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to add Aptitude Test", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
    }
}
