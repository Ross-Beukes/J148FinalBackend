
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contractor_performance.resource;

import com.j148.backend.contractor_performance.model.ContractorPerformance;
import com.j148.backend.contractor_performance.service.ContractorPerformanceService;
import com.j148.backend.contractor_performance.service.ContractorPerformanceServiceImpl;
import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.ContractorPerformanceNotFoundException;
import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.user.model.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
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
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arshr
 */
@RequestScoped
@Path("contractor-performance")
public class ContractorPerformanceResource {

    @Inject
    private ContractorPerformanceService contractorPerformanceService;
    @Inject
    private GenerateExcelFile generateExcelFile;
    private static final Logger LOG = Logger.getLogger(ContractorPerformanceResource.class.getName());

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON) // Specify that the response will be JSON
    @Path("get-contractor-performance")
// The user object will come with a user id
    public Response getContractorPerformance(User user) {
        try {
            return Response.ok(this.contractorPerformanceService.getContractorPerformance(user)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "There was an error getting the user from the database", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (ContractorPerformanceNotFoundException e) {
            LOG.log(Level.SEVERE, "The contractor performance object is null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        } catch (ContractorNotFoundException e) {
            LOG.log(Level.SEVERE, "The contractor object is null or the contractorID is null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        } catch (UserNotFoundException e) {
            LOG.log(Level.SEVERE, "User is null or the userId is null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @Path("get-all-contractor-performance")
    public Response getAllContractorPerformance() {
        try {
            return Response.ok(this.contractorPerformanceService.getAllContractorPerformance()).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "There was an error retrieving the list from the database", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (ContractorPerformanceNotFoundException e) {
            LOG.log(Level.SEVERE, "ContractorPerformance was null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (UserNotFoundException e) {
            LOG.log(Level.SEVERE, "User is null or userID is null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (ContractorNotFoundException e) {
            LOG.log(Level.SEVERE, "Contractor is null or contractorID is null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        }
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("get-all-filtered-contractor-performance")
    public Response getFilteredContractorPerformance(@QueryParam("filters") String filters) {
        try {
            List<ContractorPerformance> cp = this.contractorPerformanceService.getAllContractorPerformance();
            return Response.ok(this.contractorPerformanceService.filterContractorPerformance(filters, cp)).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "There was an error retrieving the list from the database", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (ContractorPerformanceNotFoundException e) {
            LOG.log(Level.SEVERE, "ContractorPerformance was null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (UserNotFoundException e) {
            LOG.log(Level.SEVERE, "User is null or userID is null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (ContractorNotFoundException e) {
            LOG.log(Level.SEVERE, "Contractor is null or contractorID is null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        }
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("get-contractor-list")
    public Response getListOfContractors() {
        try {
            return Response.ok(this.contractorPerformanceService.getAllContractors()).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "There was an error retrieving the list from the database", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (ContractorPerformanceNotFoundException e) {
            LOG.log(Level.SEVERE, "ContractorPerformance was null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (UserNotFoundException e) {
            LOG.log(Level.SEVERE, "User is null or userID is null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (ContractorNotFoundException e) {
            LOG.log(Level.SEVERE, "Contractor is null or contractorID is null", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        }
    }

//    @GET
//    @Path("download-report")
//    public Response downloadFile() {
//        try {
//            return Response.ok(generateExcelFile.downloadReportFile()).build();
//        } catch (ContractorPerformanceNotFoundException ex) {
//            Logger.getLogger(ContractorPerformanceResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
//        } catch (UserNotFoundException ex) {
//            Logger.getLogger(ContractorPerformanceResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
//        } catch (ContractorNotFoundException ex) {
//            Logger.getLogger(ContractorPerformanceResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
//        } catch (SQLException ex) {
//            Logger.getLogger(ContractorPerformanceResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
//        } catch (IOException ex) {
//            Logger.getLogger(ContractorPerformanceResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
//        }
//    }
    @GET
    @Path("download-report")
    public Response downloadFile() {
        try {
            byte[] fileContent = generateExcelFile.downloadReportFile();
            return Response.ok(fileContent)
                    .type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=\"report.xlsx\"")
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(ContractorPerformanceResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        }
    }

}
