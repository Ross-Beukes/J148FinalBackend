/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.resource;

import com.j148.backend.Exceptions.LeaveRequestNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.service.ContractorService;
import com.j148.backend.contractor.service.ContractorServiceImpl;
import com.j148.backend.leave_request.model.LeaveRequest;
import com.j148.backend.leave_request.service.LeaveRequestService;
import com.j148.backend.leave_request.service.LeaveRequestServiceImpl;
import com.j148.backend.user.model.User;
import com.j148.backend.user.service.UserService;
import com.j148.backend.user.service.UserServiceImpl;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.MediaType.*;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yusuf
 */

@Path("leave-request")
public class LeaveRequestResource {

    private LeaveRequestService leaveRequestService = new LeaveRequestServiceImpl();
    private UserService userService = new UserServiceImpl();
    private ContractorService contractorService = new ContractorServiceImpl();
    private static final Logger LOG = Logger.getLogger(LeaveRequestResource.class.getName());

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Path("submit-leave-request")
    public Response submitLeaveRequest(LeaveRequest leaveRequest) {
        try {
            return Response.ok(this.leaveRequestService.createLeaveRequest(leaveRequest)).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        }
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("get-leave-requests-by-date-range/{start-date}/{end-date}")
    public Response getLeaveRequestsInDateRange(@PathParam("start-date") String startDate, @PathParam("end-date") String endDate) {
        try {
            return Response.ok(this.leaveRequestService.retrieveAllLeaveRequestsBetweenDates(LocalDate.parse(startDate), LocalDate.parse(endDate))).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        }
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("get-leave-requests-by-contractor/{email}")
    public Response getLeaveRequestsByContractor(@PathParam("email") String email) {
        try {
            User user = User.builder().email(email).build();
            User foundUser = userService.findUserByEmail(user);
            Contractor contractor = Contractor.builder().user(foundUser).build();
            return Response.ok(leaveRequestService.retrieveAllContractorLeaveRequests(contractorService.retrieveContractorByUserID(contractor))).build();
        } catch (Exception ex) {
            Logger.getLogger(LeaveRequestResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        }
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("get-pending-contractor-leave-requests/{email}")
    public Response getPendingLeaveRequestsByContractor(@PathParam("email") String email) {
        try {
            User user = User.builder().email(email).build();
            User foundUser = userService.findUserByEmail(user);
            Contractor contractor = Contractor.builder().user(foundUser).build();
            return Response.ok(leaveRequestService.retrieveAllPendingContractorLeaveRequests(contractorService.retrieveContractorByUserID(contractor))).build();
        } catch (Exception ex) {
            Logger.getLogger(LeaveRequestResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        }
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("get-leave-requests-by-decision/{decision}")
    public Response getLeaveRequestsByDecision(@PathParam("decision") String decision) {
        try {
            return Response.ok(leaveRequestService.retrieveAllLeaveRequestsByDecision(decision)).build();
        } catch (Exception ex) {
            Logger.getLogger(LeaveRequestResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        }
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Path("update-leave-request-decision")
    public Response updateLeaveRequestDecision(LeaveRequest leaveRequest) {
        try {
            return Response.ok(leaveRequestService.updateLeaveRequestDecision(leaveRequest)).build();
        } catch (Exception ex) {
            Logger.getLogger(LeaveRequestResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();

        }
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("retrieve-all-leave-request")
    public Response retrieveAllLeaveRequest() {
        try {
            // Successful response
            return Response.ok(leaveRequestService.retrieveAllLeaveRequest()).build();
        } catch (LeaveRequestNotFoundException e) {
            // 404 - No leave requests found
            Logger.getLogger(LeaveRequestResource.class.getName())
                    .log(Level.WARNING, "No leave requests found: {0}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No leave requests found.")
                    .build();
        } catch (Exception e) {
            // 500 - Server error
            Logger.getLogger(LeaveRequestResource.class.getName())
                    .log(Level.SEVERE, "Error retrieving leave requests: {0}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred while retrieving leave requests.")
                    .build();
        }
    }
}
