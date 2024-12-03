package com.j148.backend.contract.repo;

import com.j148.backend.contract.model.Contract;
import com.j148.backend.user.model.User;

import java.sql.SQLException;
import java.util.Optional;

public interface ContractRepo {
    

     Optional<Contract> createContract(Contract contract) throws SQLException;
    

     Optional<Contract> findContract(long contractId) throws SQLException;


    // Update ContractRepoImpl.java
    Optional<Contract> findActiveContractOffer(User user) throws SQLException;


     Optional<Contract> updateContract(Contract contract) throws SQLException;
    
    
    
    
}
