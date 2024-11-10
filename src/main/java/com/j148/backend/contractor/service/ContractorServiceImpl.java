package com.j148.backend.contractor.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.contractor.repo.ContractorRepoImpl;

public class ContractorServiceImpl implements ContractorService {

    private final ContractorRepo contractorRepo = new ContractorRepoImpl();

    @Override
    public Contractor changeContractorStatus(Contractor contractor) throws Exception {

        if (contractorRepo.findById(contractor).isEmpty()) {

            throw new Exception("The contractor does not exist.");

        }

        if (contractor.getStatus() == null) {

            throw new IllegalArgumentException("The contract does not have a status.");

        }

        if (contractor.getContractorId() == null) {

            throw new IllegalArgumentException("The contractor is null.");
        }

        return contractorRepo.updateStatus(contractor).orElseThrow(() -> new Exception("Failed to change contractor status"));

    }

}
