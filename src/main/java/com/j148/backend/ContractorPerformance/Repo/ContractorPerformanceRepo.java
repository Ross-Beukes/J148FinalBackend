package com.j148.backend.ContractorPerformance.Repo;

import com.j148.backend.ContractorPerformance.model.ContractorPerformance;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.user.model.User;
import com.j148.backend.warning.model.Warning;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ContractorPerformanceRepo {
    Optional<ContractorPerformance>getContractorPerformance(User user)throws SQLException;
    List<ContractorPerformance>getAllContractorPerformance()throws SQLException;


}
