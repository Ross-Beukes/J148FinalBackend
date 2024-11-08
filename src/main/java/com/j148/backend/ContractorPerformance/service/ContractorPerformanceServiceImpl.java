package com.j148.backend.ContractorPerformance.service;

import com.j148.backend.ContractorPerformance.Repo.ContractorPerformanceRepo;
import com.j148.backend.ContractorPerformance.model.ContractorPerformance;
import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.user.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContractorPerformanceServiceImpl implements ContractorPerformanceService{

  //  private static final Logger LOG = Logger.getLogger(ContractorPerformance.class.getName());
    private ContractorPerformanceRepo contractorPerformanceRepo;

    public ContractorPerformanceServiceImpl (ContractorPerformanceRepo contractorPerformanceRepo){
        this.contractorPerformanceRepo= contractorPerformanceRepo;
    }
    @Override
    public Optional<ContractorPerformance> getContractorPerformance(User user) throws UserNotFoundException,SQLException{
       if(user==null||user.getUserId()==null){
      
       return Optional.empty();
       }
       
           Optional<ContractorPerformance> contractorPerformance= contractorPerformanceRepo.getContractorPerformance(user);
        return contractorPerformance;
       
    }

    @Override
    public List<ContractorPerformance> getAllContractorPerformance() throws Exception,SQLException {
        try{

            List<ContractorPerformance> contractorPerformance = contractorPerformanceRepo.getAllContractorPerformance();

            if (contractorPerformance.isEmpty()){
                return List.of();//return empty list
            }
            return contractorPerformance;

        }catch(SQLException e ){
            
            return List.of();//Return empty list in case of error
        }catch(Exception e){
           
            return List.of();
        }
    }

    @Override
    public boolean validateContractorPerformance(ContractorPerformance contractorPerformance) {
        if(contractorPerformance == null){
               return false;
        }
        if(contractorPerformance.getUser()==null||contractorPerformance.getUser().getUserId()==null){
            
            return false;
        }
        if(contractorPerformance.getContractor()==null|| contractorPerformance.getContractor().getContractorId()==null){
            
            return false;
        }
        if(contractorPerformance.getContractPeriod()==null||contractorPerformance.getContractPeriod().getStartDate()==null||contractorPerformance.getContractPeriod().getEndDate()==null){
           
            return false;
        }
        return true;
    }
}
