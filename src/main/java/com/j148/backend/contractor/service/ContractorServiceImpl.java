package com.j148.backend.contractor.service;

import com.j148.backend.contractor.model.Contractor;

import com.j148.backend.contractor.repo.ContractorRepo;
import java.sql.SQLException;
import java.util.Optional;

public class ContractorServiceImpl implements ContractorService{
    private final ContractorRepo contractorRepo;

    public ContractorServiceImpl(ContractorRepo contractorRepo) {
        this.contractorRepo = contractorRepo;
    }

    @Override
    public Optional<Contractor> update(Contractor contractor) throws SQLException {
        return contractorRepo.update(contractor);
    }
}
