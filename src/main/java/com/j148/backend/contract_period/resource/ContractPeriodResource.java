package com.j148.backend.contract_period.resource;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contract_period.service.ContractPeriodService;
import com.j148.backend.contract_period.service.ContractPeriodServiceImpl;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ContractPeriodResource handles HTTP requests related to contract period
 * operations.
 */
@RequestScoped
@Path("contract-period")
public class ContractPeriodResource {

    @Inject
    private ContractPeriodService contractPeriodService;

    private static final Logger LOG = Logger.getLogger(ContractPeriodResource.class.getName());

    /**
     * Saves a new contract period to the database.
     *
     * @param contractPeriod New contract period details.
     * @return HTTP Response indicating the result of the save operation.
     */
    @POST
    @Path("save-contract-period")
    @Consumes(APPLICATION_JSON)
    public Response saveContractPeriod(ContractPeriod contractPeriod) {
        try {
            if (contractPeriod == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Contract period must not be null.").build();
            }

            ContractPeriod savedContractPeriod = contractPeriodService.saveContractPeriod(contractPeriod);

            return Response.status(Response.Status.OK).entity(savedContractPeriod).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Database error occurred while saving contract period", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error occurred").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An unexpected error occurred", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error occurred").build();
        }
    }

    /**
     * Updates an existing contract period in the database.
     *
     * @param contractPeriod Updated contract period details.
     * @return HTTP Response indicating the result of the update operation.
     */
    @POST
    @Path("update-contract-period")
    @Consumes(APPLICATION_JSON)
    public Response updateContractPeriod(ContractPeriod contractPeriod) {
        try {
            if (contractPeriod == null || contractPeriod.getContractPeriodId() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Contract period and ID must not be null.").build();
            }

            ContractPeriod updatedContractPeriod = contractPeriodService.updateContractPeriod(contractPeriod);

            if (updatedContractPeriod != null) {
                return Response.ok(updatedContractPeriod).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Contract period not found").build();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Database error occurred while updating contract period", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error occurred").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An unexpected error occurred", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error occurred").build();
        }
    }

    /**
     * Finds a contract period by its name.
     *
     * @param name Name of the contract period to find.
     * @return HTTP Response containing the found contract period, or an error
     * message.
     */
    @GET
    @Path("find-period-by-name")
    public Response findContractPeriodByName(@QueryParam("name") String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Contract period name must not be null or empty.").build();
            }

            ContractPeriod contractPeriod = contractPeriodService.findContractPeriodByName(name);

            return Response.ok(contractPeriod).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Contract period not found with name: " + name).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An unexpected error occurred", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error occurred").build();
        }
    }

    /**
     * Finds a contract period by its ID.
     *
     * @param contractPeriod Contract period object containing the ID.
     * @return HTTP Response containing the found contract period, or an error
     * message.
     */
    @GET
    @Path("find-period-by-id")
    public Response findContractPeriodById(ContractPeriod contractPeriod) {
        try {
            if (contractPeriod == null || contractPeriod.getContractPeriodId() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Contract period or ID must not be null.").build();
            }

            ContractPeriod foundContractPeriod = contractPeriodService.findContractPeriodById(contractPeriod);

            return Response.ok(foundContractPeriod).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Contract period not found with ID: " + contractPeriod.getContractPeriodId()).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An unexpected error occurred", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error occurred").build();
        }
    }

    /**
     * Endpoint to retrieve the current contract period.
     *
     * @return HTTP Response containing the current contract period or error
     * message.
     */
    @GET
    @Path("current-contract-period")
    public Response getCurrentContractPeriod() {
        try {
            ContractPeriod contractPeriod = contractPeriodService.getCurrentContractPeriod();
            return Response.ok(contractPeriod).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error retrieving current contract period", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving current contract period").build();
        }
    }

    /**
     * Endpoint to retrieve the next contract period.
     *
     * @return HTTP Response containing the next contract period or error
     * message.
     */
    @GET
    @Path("next-contract-period")
    public Response getNextContractPeriod() {
        try {
            ContractPeriod contractPeriod = contractPeriodService.getNextContractPeriod();
            return Response.ok(contractPeriod).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error retrieving next contract period", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving next contract period").build();
        }
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("future-contract-periods")
    public Response getFutureContractPeriods() {
        try {
            List<ContractPeriod> contractPeriods = contractPeriodService.getAllFutureContractPeriods();
            return Response.ok(contractPeriods).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error retrieving contract periods", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving future contract periods").build();
        }

    }
}