package com.j148.backend.warning.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.contractor.repo.ContractorRepoImpl;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.repo.WarningRepo;
import com.j148.backend.warning.repo.WarningRepoImpl;

public class WarningServiceImpl implements WarningService{

    private final WarningRepo warningRepo = new WarningRepoImpl();
    private final ContractorRepo contractorRepo = new ContractorRepoImpl();
    @Override
    public Warning appealWarning(Warning warning, Contractor contractor) throws Exception {

        if (warning == null){
            throw new IllegalArgumentException("Warning is null");
        }
        if (contractor == null){
            throw new IllegalArgumentException("Contractor is null");
        }

        if (warning.getWarningId() == null ){
            throw new IllegalArgumentException("Warning id is null");
        }

        if (contractor.getContractorId() == null){
            throw new IllegalArgumentException("Contractor id is null");
        }

        if (contractorRepo.findById(contractor.getContractorId()).isEmpty()){
            throw new IllegalArgumentException("Could not find contractor");
        }

        if (warningRepo.findById(warning).isEmpty()){
            throw new IllegalArgumentException("Could not find warning");
        }

        return warningRepo.updateState(warning)
                .orElseThrow(() -> new Exception("Failed to appeal warning"));

    }
}
