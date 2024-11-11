package com.j148.backend.attendance.resource;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.attendance.service.AttendanceService;
import com.j148.backend.attendance.service.AttendanceServiceImpl;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/attendance")
public class AttendanceResource {
private static final Logger LOG = Logger.getLogger(AttendanceResource.class.getName());

private AttendanceService attendanceService;
/**
 * create a new attendance record
 *
 * @param attendance the attendance object to be inserted
 * @return Response with status of the operation
 * */
@POST
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public Response createAttendanceRecord (Attendance attendance){
    try{
        Optional<Attendance> createdAttendance = attendanceService.createAttendanceRecord(attendance);
        if (createdAttendance.isPresent()){
            return Response.status(Response.Status.CREATED)
                    .entity(createdAttendance.get())
                    .build();

        }else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Failed to create attendance record")
                    .build();
        }
    }catch (SQLException e){
        LOG.log(Level.SEVERE,"Error while create attendance record ",e);
        return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error while processing the request.")
                .build();
    }
}
/**
 * Retrieve an attendance record by its ID
 *
 * @param id the attendance with attendance record or error message.
 * */
@GET
@Path("/{id}")
@Produces(APPLICATION_JSON)
public Response getAttendanceById(@PathParam("id")Long id){
    try {
        Optional<Attendance>attendance =attendanceService.getAttendanceById(id);
    if (attendance.isPresent()){
        return Response.status(Response.Status.OK)
                .entity(attendance.get())
                .build();
    }else {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Attendance Record not found ")
                .build();
    }

    }catch (SQLException e){
        LOG.log(Level.SEVERE,"Error while retrieving attendance record with ID"+id+e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error while processing the request")
                .build();
    }
}
/**
 * Update an existing attendance record
 *
 * @param attendance the attendance object with the updated details
 * @return Response with the status of the update operation
 *
 * */
@PUT
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public Response updateAttendance(Attendance attendance){
    try{
        Optional<Attendance> updateAttendance =attendanceService.updateAttendance(attendance);
        if (updateAttendance.isPresent()){
            return Response.status(Response.Status.OK)
                    .entity(updateAttendance.get())
                    .build();

        }else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Failed to update Record ")
                    .build();
        }
    }catch (SQLException e){
LOG.log(Level.SEVERE,"Error while updating attendance record ",e);
return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity("Error while processing request")
        .build();
    }
}

/**
 * Retrieve all attendance records
 *
 * @reurn Response with a list of all attendance records */
public Response getAllAttendanceRecords(){
    try{
        List<Attendance>attendanceList= attendanceService.getAllAttendanceRecords();
        if (!attendanceList.isEmpty()){
            return Response.status(Response.Status.OK)
                    .entity(attendanceList)
                    .build();
        }else{
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No attendance record found ")
                    .build();
        }
    }catch (SQLException e){
        LOG.log(Level.SEVERE,"Error while retrieving all attendance records",e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error while processing the request.")
                .build();
    }
}
/**
 * Retrieve all attendance records for a specific contractor
 *
 * @param contractor_id the contractors ID
 * @return Response with the attendance records of the contractor */
public Response getAllAttendanceForContractor(@PathParam("contractor_id")Long contractor_id){
    try{
        Optional<Attendance> contractorAttendance = attendanceService.FindAllAttendanceForContractor(contractor_id);
        if(contractorAttendance.isPresent()){
            return Response.status(Response.Status.OK)
                    .entity(contractorAttendance.get())
                    .build();
        }else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No attendance records found gor this contractor")
                    .build();
        }
    }catch (SQLException e){
        LOG.log(Level.SEVERE,"Error while retrieving attendance for this contractor ",e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error while processing the request.")
                .build();
    }
}

}
