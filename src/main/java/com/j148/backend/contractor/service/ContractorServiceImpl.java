package com.j148.backend.contractor.service;

import com.j148.backend.contractor.model.Contractor;

import com.j148.backend.contractor.repo.ContractorRepository;
import java.sql.SQLException;
import java.util.Optional;

public class ContractorServiceImpl implements ContractorService{
    private final ContractorRepository contractorRepository;

    public ContractorServiceImpl(ContractorRepository contractorRepository) {
        this.contractorRepository = contractorRepository;
    }

    @Override
    public Optional<Contractor> update(Contractor contractor) throws SQLException {
        return contractorRepository.update(contractor);
    }
}
