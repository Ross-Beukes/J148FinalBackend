package com.j148.backend.contractor.service;

import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.contractor.model.Contractor;

import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.contractor.repo.ContractorRepoImpl;

import java.util.Optional;


public class ContractorServiceImpl implements ContractorService{

   
    private final ContractorRepo contractorRepo = new ContractorRepoImpl();

  
    public Contractor updateContractor(Contractor contractor) throws Exception{
        if(contractor == null){
            throw new UserNotFoundException("User not found, update failed");

        } else {
            Optional<Contractor> updatedContractor = contractorRepo.update(contractor);
            return updatedContractor.orElseThrow(() -> new UserNotFoundException("User not found, update failed"));
        }
    }

}
