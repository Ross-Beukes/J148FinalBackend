package com.j148.backend.contract.repo;

import com.j148.backend.contract.model.Contract;
import java.sql.SQLException;
import java.util.Optional;

public interface ContractRepo {
    
    /**
     * The create Contract method adds a new Contract record to the HRMS Database
     * 
     *@param Contract
     *@return Optional<Contract>
     */ 
    public Optional<Contract> createContract(Contract contract) throws SQLException;
    
    /**
     * The find Contract method finds a Contract record from the HRMS Database
     * through an contractId
     * 
     *@param long contractId
     *@return Optional<Contract>
     */ 
    public Optional<Contract> findContract(long contractId) throws SQLException;
    
    
    /**
     * The update Contract method updates an existing Contract record in the HRMS Database
     * 
     *@param Contract
     *@return Optional<Contract>
     */ 
    public Optional<Contract> updateContract(Contract contract) throws SQLException;
    
    
    
    
}
