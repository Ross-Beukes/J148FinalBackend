/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.resource;

import com.j148.backend.contractor.service.ContractorService;
import com.j148.backend.contractor.service.ContractorServiceImpl;
import com.j148.backend.leave_request.model.LeaveRequest;
import com.j148.backend.leave_request.service.LeaveRequestService;
import com.j148.backend.leave_request.service.LeaveRequestServiceImpl;
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
    public Response submitLeaveRequest(LeaveRequest leaveRequest){
        try {
            return Response.ok(this.leaveRequestService.createLeaveRequest(leaveRequest)).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        }
    }
    
    @GET
    @Produces(APPLICATION_JSON)
    @Path("get-leave-requests/{start-date}/{end-date}")
    public Response getLeaveRequestsInDateRange(@PathParam("start-date") LocalDate startDate, @PathParam("end-date") LocalDate endDate){
        try {
            return Response.ok(this.leaveRequestService.retrieveAllLeaveRequestsBetweenDates(startDate, endDate)).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        }
    }
    
//    @GET
//    @Produces(APPLICATION_JSON)
//    @Path("get-leave-requests/{email}")
//    public Response getLeaveRequestsByContractor(@PathParam("email") String email){
//        
//    }
    
}
