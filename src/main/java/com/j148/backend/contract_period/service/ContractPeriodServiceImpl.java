/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contract_period.service;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contract_period.repo.ContractPeriodRepo;
import com.j148.backend.contract_period.repo.ContractPeriodRepoImpl;
import java.sql.SQLException;

/**
 *
 * @author glenl
 */
public class ContractPeriodServiceImpl implements ContractPeriodService {
    
    private ContractPeriodRepo contractPeriodRepo = new ContractPeriodRepoImpl();
    
    @Override
    public ContractPeriod getCurrentContractPeriod() throws SQLException, Exception {
        return contractPeriodRepo.getCurrentContractPeriod().orElseThrow(() -> new Exception("Contract Period not found"));
    }

    @Override
    public ContractPeriod getNextContractPeriod() throws SQLException, Exception {
        return contractPeriodRepo.getNextContractPeriod().orElseThrow(() -> new Exception("Next Contract Period not found"));
    }
    
}
