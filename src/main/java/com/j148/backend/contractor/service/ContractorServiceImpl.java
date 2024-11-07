package com.j148.backend.contractor.service;

import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.contractor.model.Contractor;

import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.contractor.repo.ContractorRepoImpl;


public class ContractorServiceImpl implements ContractorService{

   
    private final ContractorRepo contractorRepo = new ContractorRepoImpl();

  
    public Contractor updateContractor(Contractor contractor) throws Exception{
        if(contractor != null){
            return contractorRepo.update(contractor).get();
        }else{
            throw new UserNotFoundException("user not found update failed");
        }
    }
}
