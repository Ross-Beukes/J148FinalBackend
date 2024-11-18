package com.j148.backend.contractor.resource;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.service.ContractorService;
import com.j148.backend.contractor.service.ContractorServiceImpl;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * ContractorResource handles HTTP requests related to contractor operations.
 */
@Path("contractor")
public class ContractorResource {

    private final ContractorService contractorService = new ContractorServiceImpl();
    private static final Logger LOG = Logger.getLogger(ContractorResource.class.getName());

    /**
     * Updates an existing contractor in the database.
     * @param contractorId ID of the contractor to update.
     * @param contractor   Updated contractor details.
     * @return HTTP Response indicating the result of the update operation.
     */
    @POST
    @Path("update")
    @Consumes(APPLICATION_JSON)
    public Response updateContractor(Contractor contractor) {
        try {

            Contractor updatedContractor = contractorService.updateContractor(contractor);
            if (updatedContractor != null) {
                return Response.ok(updatedContractor).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Contractor not found").build();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Database error occurred while updating contractor", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("unexpected error occurred").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An unexpected error occurred", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error occurred").build();
        }
    } /**
     * Updates the status of an existing contractor in the database.
     *
     * @param contractorId ID of the contractor to update.
     * @param contractor   Contractor object containing the updated status.
     * @return HTTP Response indicating the result of the status update operation.
     */
    @POST
    @Path("changeStatus/{contractorId}")
    @Consumes(APPLICATION_JSON)
    public Response changeContractorStatus(@PathParam("contractorId") long contractorId, Contractor contractor) {
        try {
            contractor.setContractorId(contractorId);
            Contractor updatedContractor = contractorService.changeContractorStatus(contractor);
            if (updatedContractor != null) {
                return Response.ok(updatedContractor).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Contractor not found").build();
            }
        } catch (IllegalArgumentException e) {
            LOG.log(Level.WARNING, "Invalid contractor status or ID", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid contractor status or ID").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "An unexpected error occurred while changing contractor status", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error occurred").build();
        }
    }
}

