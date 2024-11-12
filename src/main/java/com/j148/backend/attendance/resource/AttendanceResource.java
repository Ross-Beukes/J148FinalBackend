package com.j148.backend.attendance.resource;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.attendance.service.AttendanceService;
import com.j148.backend.attendance.service.AttendanceServiceImpl;
import com.j148.backend.contractor.model.Contractor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class AttendanceResource{

    private static  final Logger LOG = Logger.getLogger(AttendanceResource.class.getName());
    private final AttendanceService attendanceService = new AttendanceServiceImpl();

    /**
     *Check in for a contractor - Creates attendance record.
     *
     * @param attendance The attendance record details.
     * @return Response with status of the operation
     **/
    @POST
    @Path("/checkin")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response checkIn(Attendance attendance){
        try{
            Attendance createdAttendance = attendanceService.createAttendenceRecord(attendance);
            return Response.status(Response.Status.CREATED)
                    .entity(createdAttendance)
                    .build();
        } catch (SQLException e){
            LOG.log(Level.SEVERE,"Error while creating attendance record",e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error while processing the check-in request.")
                    .build();
        } catch (Exception e){
            LOG.log(Level.SEVERE,"Error while creating attendance record",e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error while processing the check-in request.")
                    .build();
        }
    }
    /**
     * Check out of for a contractor - Updates the time-out in an attendance record.
     *
     * @param attendance The attendance record to be checked out
     * @return Response with the status of the update operation*/
    @POST
    @Path("/checkout")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response checkOut(Attendance attendance){
    try{
        Attendance updatedAttendance = attendanceService.checkOut(attendance);
        return   Response.status(Response.Status.OK)
                .entity(updatedAttendance)
                .build();
    } catch(SQLException e){
        LOG.log(Level.SEVERE,"Error while checking out attendance record",e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error while processing the check-out request")
                .build();

    } catch(Exception e){
        LOG.log(Level.SEVERE,"Error while checking out attendance record",e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error while processing the check-out request")
                .build();

    }
    }
    /**
     * Mark absent contractors for today
     *
     * @return Response with a list of absent attendance records*/
    @POST
    @Path("/absent")
    @Produces(APPLICATION_JSON)
        public Response markAbsentContractors(){
        try{
            List<Attendance>absentContractors =attendanceService.createAbsentContractors();
            return Response.status(Response.Status.OK)
                    .entity(absentContractors)
                    .build();

        }catch (SQLException e){
            LOG.log(Level.SEVERE, "Error while absent contractors ",e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error while processing absent contractors")
                    .build();

        }catch (Exception e ){
            LOG.log(Level.SEVERE, "Error while absent contractors ",e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error while processing absent contractors")
                    .build();
        }
    }
    /**
     * Get list of contractors who haven't checked in today.
     *
     * @param contractors List of contractors to check
     * @return Response with list of contractors who haven't checked in today*/

    @POST
    @Path("/notcheckedin")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response getContractorsNotCheckedIn(List<Contractor>contractors){
        try{
            List<Attendance>notCheckedIn= attendanceService.contractorsNotCheckedIn(contractors);
            return Response.status(Response.Status.OK)
                    .entity(notCheckedIn)
                    .build();

        }catch (SQLException e){
            LOG.log(Level.SEVERE,"Error while retrieving contractors not checked in");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error while processing contractor not checked in")
                    .build();

        }catch(Exception e){
            LOG.log(Level.SEVERE,"Error while retrieving contractors not checked in");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error while processing contractor not checked in")
                    .build();

        }
    }








}