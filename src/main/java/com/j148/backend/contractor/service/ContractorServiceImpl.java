package com.j148.backend.contractor.service;

import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contract_period.service.ContractPeriodService;
import com.j148.backend.contract_period.service.ContractPeriodServiceImpl;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.contractor.repo.ContractorRepoImpl;
import com.j148.backend.contractor.service.ContractorService;
import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import com.j148.backend.user.repo.UserRepoImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * @author glenl
 */
public class ContractorServiceImpl implements ContractorService {

    private ContractorRepo contractorRepo = new ContractorRepoImpl();
    private final ContractPeriodService contractPeriodService = new ContractPeriodServiceImpl();
    private UserRepo userRepo = new UserRepoImpl();

    @Override
    public List<Contractor> findCurrentContractors() throws SQLException, Exception {
        ContractPeriod contractPeriod = contractPeriodService.getCurrentContractPeriod();
        return contractorRepo.findCurrentContractor(contractPeriod);
    }

    @Override
    public Contractor promoteToContractor(User user) throws SQLException, Exception {
        Contractor contractor = Contractor.builder().build();
        if (user != null) {
            User promotedUser = userRepo.promoteApplicant(user).orElseThrow(() -> new Exception("The user was not promoted"));
            contractor.setUser(promotedUser);
            contractor.setStatus(Contractor.Status.ACTIVE);
            ContractPeriod contractPeriod = contractPeriodService.getNextContractPeriod();
            contractor.setContractPeriod(contractPeriod);
            return contractorRepo.save(contractor).orElseThrow(() -> new Exception("Contractor could not be saved"));
        } else {
            throw new IllegalArgumentException("User is null");
        }
    }

    @Override
    public Contractor promoteToExternalContractor(Contractor contractor) throws SQLException, Exception {
        if (contractor != null) {
            contractor.setStatus(Contractor.Status.EXTERNAL);
            return contractorRepo.updateStatus(contractor).orElseThrow(() -> new Exception("Contractor could not be updated"));
        } else {
            throw new IllegalArgumentException("Contractor is null");
        }
    }

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

    public Contractor updateContractor(Contractor contractor) throws Exception{
        if(contractor == null){
            throw new UserNotFoundException("User not found, update failed");

        } else {
            Optional<Contractor> updatedContractor = contractorRepo.updateStatus(contractor);
            return updatedContractor.orElseThrow(() -> new UserNotFoundException("User not found, update failed"));
        }
    }
}