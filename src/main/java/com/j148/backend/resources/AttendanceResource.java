package com.j148.backend.resources;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.attendance.service.AttendanceService;
import com.j148.backend.attendance.service.AttendanceServiceImpl;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("attendance")
public class AttendanceResource {

    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());
    private AttendanceService attendanceService = new AttendanceServiceImpl();

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
}
