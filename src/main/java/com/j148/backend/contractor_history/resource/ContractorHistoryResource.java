/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contractor_history.resource;

import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepoImpl;
import com.j148.backend.contractor_history.service.ContractorHistoryService;
import com.j148.backend.contractor_history.service.ContractorHistoryServiceImpl;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tshireletso
 */
@RequestScoped
@Path("contractor-history")
public class ContractorHistoryResource {
    
    private final ContractorHistoryService contractorHistoryService= new ContractorHistoryServiceImpl();
    private static final Logger LOG = Logger.getLogger(ContractorHistoryResource.class.getName());
    private final ContractorRepoImpl c = new ContractorRepoImpl();
   
    
    
    @GET
    @Path("ping")
    public Response pingResource(){
        return Response.ok("Successfully pinged User Resource").build();
    }
    
    @GET
    @Path("disciplinary-history")
    public Response getDisciplinaryHistory(Contractor contractor){
       
    if(contractor != null)   
    
    {try {
            if(this.contractorHistoryService.viewWarningAndHearingHistory(contractor) != null){
            return Response.ok(this.contractorHistoryService.viewWarningAndHearingHistory(contractor)).build();   
            }
            else{
            throw new RuntimeException("Unfortunately no history was returned object is null ??");
            }
      }   
        catch (ContractorNotFoundException e) {
        LOG.log(Level.SEVERE, "The contractor object is null or the contractorID is null", e);
        return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
    } catch (UserNotFoundException e) {
        LOG.log(Level.SEVERE, "User is null or the userId is null", e);
        return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
    }
        catch (SQLException e) {
        LOG.log(Level.SEVERE, "There was an error getting the user from the database", e);
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid").build();
        } catch (Exception ex) {
            Logger.getLogger(ContractorHistoryResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }else{
        throw new UserNotFoundException("Contractor was not found");
    }
        
        return null;
    
        
    }
    
    
}
