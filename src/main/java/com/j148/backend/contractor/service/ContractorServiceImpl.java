/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contractor.service;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contract_period.service.ContractPeriodService;
import com.j148.backend.contract_period.service.ContractPeriodServiceImpl;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.contractor.repo.ContractorRepoImpl;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author glenl
 */
public class ContractorServiceImpl implements ContractorService {

    private ContractorRepo contractorRepo = new ContractorRepoImpl();
    private ContractPeriodService contractPeriodService = new ContractPeriodServiceImpl();

    @Override
    public List<Contractor> findCurrentContractors() throws SQLException, Exception {
        ContractPeriod contractPeriod = contractPeriodService.getCurrentContractPeriod();
        return contractorRepo.findCurrentContractor(contractPeriod);
    }
    
    

}
