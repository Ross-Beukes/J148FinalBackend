package com.j148.backend.contractor.service;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contract_period.service.ContractPeriodService;
import com.j148.backend.contract_period.service.ContractPeriodServiceImpl;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.contractor.repo.ContractorRepoImpl;
import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepo;
import com.j148.backend.user.repo.UserRepoImpl;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author glenl
 */
public class ContractorServiceImpl implements ContractorService {

    private ContractorRepo contractorRepo = new ContractorRepoImpl();
    private ContractPeriodService contractPeriodService = new ContractPeriodServiceImpl();
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
            return contractorRepo.update(contractor).orElseThrow(() -> new Exception("Contractor could not be updated"));
        } else {
            throw new IllegalArgumentException("Contractor is null");
        }

    }

}
