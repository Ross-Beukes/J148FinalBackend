package com.j148.backend.contract_period.service;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contract_period.repo.ContractPeriodRepo;
import com.j148.backend.contract_period.repo.ContractPeriodRepoImpl;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ContractPeriodServiceImpl implements ContractPeriodService {

    private final ContractPeriodRepo contractPeriodRepo = new ContractPeriodRepoImpl();

    @Override
    public ContractPeriod saveContractPeriod(ContractPeriod contractPeriod) throws Exception {
        if (contractPeriod == null) {
            throw new IllegalArgumentException("Contract period must not be null.");
        }

        Optional<ContractPeriod> savedContractPeriod = contractPeriodRepo.saveContractPeriod(contractPeriod);
        return savedContractPeriod.orElseThrow(() ->
                new IllegalStateException("Failed to save contract period. Please ensure all fields are correctly filled and formatted.")
        );
    }

    @Override
    public ContractPeriod findContractPeriodByName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Contract period name must not be null or empty.");
        }

        Optional<ContractPeriod> contractPeriod = contractPeriodRepo.findContractPeriodByName(name);
        return contractPeriod.orElseThrow(() ->
                new NoSuchElementException("No contract period found with name: " + name)
        );
    }

    @Override
    public ContractPeriod findContractPeriodById(ContractPeriod contractPeriod) throws Exception {
        if (contractPeriod == null || contractPeriod.getContractPeriodId() == null) {
            throw new IllegalArgumentException("Contract period or contract period ID cannot be null.");
        }
        Optional<ContractPeriod> foundContractPeriod = contractPeriodRepo.findById(contractPeriod);

        return foundContractPeriod.orElseThrow(() ->
                new NoSuchElementException("No contract period found with ID: " + contractPeriod.getContractPeriodId())
        );
    }

    @Override
    public ContractPeriod updateContractPeriod(ContractPeriod contractPeriod) throws Exception {
        if (contractPeriod == null || contractPeriod.getContractPeriodId() == null) {
            throw new IllegalArgumentException("Contract period cannot be null.");
        }
        Optional<ContractPeriod> existingContractPeriod = contractPeriodRepo.findById(contractPeriod);

        if (existingContractPeriod.isPresent()) {
            Optional<ContractPeriod> updatedContractPeriod = contractPeriodRepo.updateContractPeriod(contractPeriod);
            return updatedContractPeriod.orElseThrow(() ->
                    new IllegalStateException("Failed to update contract period with ID: " + contractPeriod.getContractPeriodId())
            );

        } else {
            throw new NoSuchElementException("No contract period found with ID: " + contractPeriod.getContractPeriodId());
        }
    }

    @Override
    public ContractPeriod getCurrentContractPeriod() throws SQLException, Exception {
        return contractPeriodRepo.getCurrentContractPeriod().orElseThrow(() -> new Exception("Contract Period not found"));
    }

    @Override
    public ContractPeriod getNextContractPeriod() throws SQLException, Exception {
        return contractPeriodRepo.getNextContractPeriod().orElseThrow(() -> new Exception("Next Contract Period not found"));
    }
    @Override
    public double enrollmentAveragesForYear(int year) throws SQLException {
        return contractPeriodRepo.enrollmentAveragesForYear(year);
    }

    @Override
    public double enrollmentAverageForPeriodOfYears(int startYear, int endYear) throws SQLException {
        return contractPeriodRepo.enrollmentAverageForPeriodOfYears(startYear, endYear);
    }


}