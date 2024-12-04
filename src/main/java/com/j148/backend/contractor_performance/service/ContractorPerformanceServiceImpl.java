package com.j148.backend.contractor_performance.service;

import com.j148.backend.contractor_performance.repo.ContractorPerformanceRepo;
import com.j148.backend.contractor_performance.repo.ContractorPerformanceRepoImpl;
import com.j148.backend.contractor_performance.model.ContractorPerformance;
import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.ContractorPerformanceNotFoundException;
import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class ContractorPerformanceServiceImpl implements ContractorPerformanceService {

    //  private static final Logger LOG = Logger.getLogger(ContractorPerformance.class.getName());
    @Inject
    private ContractorPerformanceRepo contractorPerformanceRepo;

    @Override
    public ContractorPerformance getContractorPerformance(User user) throws ContractorPerformanceNotFoundException, UserNotFoundException, ContractorNotFoundException, SQLException {
        if (user == null || user.getUserId() == null || user.getUserId() == 0) {
            throw new UserNotFoundException("The user object was equal to null or rhe userId was null");
        }

        Optional<ContractorPerformance> contractorPerformanceOptional = contractorPerformanceRepo.getContractorPerformance(user);
        ContractorPerformance contractorPerformance = contractorPerformanceOptional.orElseThrow(() -> new IllegalStateException("Contractor performance optional is empty. Contractor not found"));

        validateContractorPerformance(contractorPerformance);
        return contractorPerformance;

    }

    @Override
    public List<ContractorPerformance> getAllContractorPerformance() throws ContractorPerformanceNotFoundException, UserNotFoundException, ContractorNotFoundException, SQLException {
        List<ContractorPerformance> contractorPerformance = contractorPerformanceRepo.getAllContractorPerformance();

        if (contractorPerformance.isEmpty()) {
            return List.of();//return empty list
        }

        for (ContractorPerformance cp : contractorPerformance) {
            validateContractorPerformance(cp);
        }
        return contractorPerformance;

    }

    @Override
    public List<ContractorPerformance> filterContractorPerformance(String filters, List<ContractorPerformance> cp) throws ContractorPerformanceNotFoundException, UserNotFoundException, ContractorNotFoundException, SQLException {
        if (cp == null || cp.isEmpty()) {
            throw new ContractorPerformanceNotFoundException("The list of contractors is empty or is equal to null");
        }
        if (filters == null || filters.isEmpty() || filters.isBlank()) {
            return cp;
        }

        List<ContractorPerformance> filteredContractorPerformances = contractorPerformanceRepo.filterContractorPerformance(filters, cp);

        for (ContractorPerformance person : filteredContractorPerformances) {
            validateContractorPerformance(person);
        }

        return filteredContractorPerformances;

    }

    @Override
    public List<ContractorPerformance> getAllContractors() throws ContractorPerformanceNotFoundException, UserNotFoundException, ContractorNotFoundException, SQLException {
        List<ContractorPerformance> contractorPerformance = contractorPerformanceRepo.getAllContractors();

        if (contractorPerformance.isEmpty()) {
            return List.of();//return empty list
        }

        for (ContractorPerformance cp : contractorPerformance) {
            validateContractorPerformance(cp);
        }
        return contractorPerformance;

    }

    public void validateContractorPerformance(ContractorPerformance contractorPerformance) throws ContractorPerformanceNotFoundException, UserNotFoundException, ContractorNotFoundException {
        if (contractorPerformance == null) {
            throw new ContractorPerformanceNotFoundException("Contractor Performance Object is equal to null");
        }
        if (contractorPerformance.getUser() == null || contractorPerformance.getUser().getUserId() == null || contractorPerformance.getUser().getUserId() == 0) {
            throw new UserNotFoundException("User object is equal to null or the userId is 0");
        }
        if (contractorPerformance.getContractor() == null || contractorPerformance.getContractor().getContractorId() == null || contractorPerformance.getContractor().getContractorId() == 0) {
            throw new ContractorNotFoundException("The contractor object is null or the contractor id is 0 ");
        }
    }

}
