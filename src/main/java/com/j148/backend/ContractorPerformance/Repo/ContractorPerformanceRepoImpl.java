package com.j148.backend.ContractorPerformance.Repo;

import com.j148.backend.ContractorPerformance.model.ContractorPerformance;
import com.j148.backend.config.DBConfig;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.user.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class ContractorPerformanceRepoImpl extends DBConfig implements ContractorPerformanceRepo {

    private static final String URL= "jdbc:mysql://localhost:3306/hrms?autoReconnect=true&useSSL=false";

    @Override
    public Optional<ContractorPerformance> getContractorPerformance(User user) throws SQLException {
    String query="";


        return
    }

    @Override
    public Optional<ContractorPerformance> getAllContractorPerformance() throws SQLException {
        String query = "";


        return Optional.of(contractorPerformance);

    }
}
