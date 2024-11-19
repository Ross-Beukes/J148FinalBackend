/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contract.resource;

import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.aptitude_test.service.AptitudeTestService;
import com.j148.backend.aptitude_test.service.AptitudeTestServiceImpl;
import com.j148.backend.contract.service.ContractService;
import com.j148.backend.contract.service.ContractServiceImpl;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.files.service.FileEntityService;
import com.j148.backend.files.service.FileEntityServiceImpl;
import com.j148.backend.user.model.User;
import com.j148.backend.user.service.UserService;
import com.j148.backend.user.service.UserServiceImpl;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yusuf
 */
@RequestScoped
@Path("contract")
public class ContractResource {

    @Inject
    private ContractService contractService;
    @Inject
    private UserService userService;
    @Inject
    private AptitudeTestService aptitudeTestService;
    @Inject
    private FileEntityService fileEntityService;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Path("offer-contract")
    public Response offerContract(User user) {
        try {
            User foundUser = userService.findUserByEmail(user);
            AptitudeTest aptitudeTest = aptitudeTestService.retrieveAptitudeTestByUserId(foundUser);
            FileEntity idFile = fileEntityService.retrieveFileByUserIdAndCategory(foundUser, FileEntity.Category.ID);
            FileEntity matricCertificateFile = fileEntityService.retrieveFileByUserIdAndCategory(foundUser, FileEntity.Category.MATRIC_CERTIFICATE);
            return Response.ok(this.contractService.offerContract(foundUser, aptitudeTest, idFile, matricCertificateFile)).build();
        } catch (Exception ex) {
            Logger.getLogger(ContractResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        }
    }

}
