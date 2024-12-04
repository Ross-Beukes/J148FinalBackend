package com.j148.backend.resources;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.attendance.service.AttendanceService;
import com.j148.backend.attendance.service.AttendanceServiceImpl;
import com.j148.backend.contractor.model.Contractor;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Path("attendance")
public class AttendanceResource {

    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());
    @Inject
    private AttendanceService attendanceService;

    @GET
    public Response pingAttendanceResource() {
        return Response.ok("Successfully pinged Attendance Resource").build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("check-in")
    public Response createAttendanceRecord(Attendance attendance) {
        try {
            return Response.ok(this.attendanceService.createAttendenceRecord(attendance)).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "the user model passed to the server is invalid", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "unable to process user login", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("check-out")
    public Response checkOut(Attendance attendance) {
        try {
            return Response.ok(this.attendanceService.checkOut(attendance)).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "the user model passed to the server is invalid", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "unable to process user login", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }

    /**
     * Get list of contractors who haven't checked in today.
     *
     *
     * @return Response with list of contractors who haven't checked in today
     */
    @GET
    @Path("not-checked-in")
    public Response getContractorsNotCheckedIn(List<Contractor> contractors) {
        try {
            List<Attendance> notCheckedIn = attendanceService.contractorsNotCheckedIn(contractors);
            return Response.status(Response.Status.OK).entity(notCheckedIn).build();

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error while retrieving contractors not checked in");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while processing contractor not checked in").build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while retrieving contractors not checked in");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while processing contractor not checked in").build();

        }
    }

    /**
     * Mark absent contractors for today
     *
     * @return Response with a list of absent attendance records
     */
    @POST
    @Path("absent")
    @Produces(APPLICATION_JSON)
    public Response markAbsentContractors() {
        try {
            List<Attendance> absentContractors = attendanceService.createAbsentContractors();
            return Response.status(Response.Status.OK).entity(absentContractors).build();

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error while absent contractors ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while processing absent contractors").build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while absent contractors ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while processing absent contractors").build();
        }
    }

    @GET
    @Path("currentAttendance")
    @Produces(APPLICATION_JSON)
    public Response retrieveAttendanceByCurrentContractors() {
        try {
            List<Attendance> currentContractors = attendanceService.retrieveAttendanceByCurrent();
            return Response.status(Response.Status.OK).entity(currentContractors).build();

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error while absent contractors ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while processing absent contractors").build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while absent contractors ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while processing absent contractors").build();
        }
    }

    @POST
    @Path("get-attendance")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response getAttendanceByContractor(Contractor contractor) {
        try {
            Attendance attendance = attendanceService.getAttendanceByContractorId(Attendance.builder().contractor(contractor).build());
            return Response.status(Response.Status.OK).entity(attendance).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error while retrieving contractors not checked in");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while processing contractor not checked in").build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while retrieving contractors not checked in");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while processing contractor not checked in").build();

        }
    }
}
