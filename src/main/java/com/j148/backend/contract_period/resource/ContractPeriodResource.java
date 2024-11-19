package com.j148.backend.contract_period.resource;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contract_period.service.ContractPeriodService;
import com.j148.backend.contract_period.service.ContractPeriodServiceImpl;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ContractPeriodResource handles HTTP requests related to contract period operations.
 */
@Path("contract-period")
public class ContractPeriodResource {

    private final ContractPeriodService contractPeriodService = new ContractPeriodServiceImpl();
    private static final Logger LOG = Logger.getLogger(ContractPeriodResource.class.getName());

}