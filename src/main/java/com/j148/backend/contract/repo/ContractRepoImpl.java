package com.j148.backend.contract.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contract.model.Contract;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contract_period.repo.ContractPeriodRepoImpl;
import com.j148.backend.user.model.User;
import com.j148.backend.user.repo.UserRepoImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Optional;

//Author : TSHIRELETSO

public class ContractRepoImpl extends DBConfig  implements ContractRepo{
    
    private final ContractPeriodRepoImpl cpri = new ContractPeriodRepoImpl();
    private final UserRepoImpl uri = new UserRepoImpl();

    @Override
    public Optional<Contract> createContract(Contract contract) throws SQLException {
        String sql = "INSERT INTO contract(contractor_period_id,user_id,decision_date,offer_date,expiration_date,decision,deleted) "
                + " VALUES(?,?,?,?,?,?,?) ";
        
        try(Connection con = getCon() ; PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
        con.setAutoCommit(false);
        Savepoint save = con.setSavepoint();
        
        try{
            
            ps.setLong(1, contract.getContractId());
            ps.setLong(2,contract.getUser().getUserId());
            ps.setString(3,String.valueOf(contract.getDecisionDate()));
            ps.setString(4,String.valueOf(contract.getOfferDate()));
            ps.setString(5,String.valueOf(contract.getExpirationDate()));
            ps.setString(6,String.valueOf(contract.getDecision()));
            ps.setBoolean(7, contract.isDeleted());
            
            if(ps.executeUpdate() > 0){
                
                try(ResultSet rs = ps.getGeneratedKeys()){
                    if(rs.next()){
                        contract.setContractId(rs.getLong(1));
                        con.commit();
                        return Optional.of(contract);
                    }
                    else{
                        con.rollback(save);
                    }
                
                }
            }
            
            
            
        
        }catch(Exception e){
         System.out.println("Error while creating a new contract");
        }
            
        }
        
        return Optional.empty();
       
    }

    @Override
    public Optional<Contract> findContract(long contractId) throws SQLException{
         String sql = "SELECT * FROM contract WHERE contract_id = ? ";
         
         try(Connection con = getCon() ; PreparedStatement ps = con.prepareStatement(sql)){
         
             ps.setLong(1,contractId);
             
             try(ResultSet rs = ps.executeQuery()){
               if(rs.next()){  
                 ContractPeriod c = cpri.findContract(rs.getLong(2)).get() ;
                 User user = new User();
                 user.setUserId(rs.getLong(3));
                 user = uri.retreiveUserFromUserID(user).get();
                 
                 Contract contract = Contract.builder().
                         contractId(contractId)
                         .contractPeriod(c)
                         .offerDate(rs.getDate(4).toLocalDate())
                         .decisionDate(rs.getDate(5).toLocalDate())
                         .expirationDate(rs.getDate(6).toLocalDate())
                         .user(user)
                         .decision(Contract.Decision.valueOf(rs.getString(7)))
                         .isDeleted(rs.getBoolean(8))
                         .build();
                 
                 return Optional.of(contract);
               }
             }
         }
         
        return Optional.empty();
    }

    @Override
    public Optional<Contract> updateContract(Contract contract) throws SQLException {
        String sql = "UPDATE contract SET contractor_period_id = ? decision_date = ? decision = ? deleted = ? "
                + "WHERE contract_id = ?";
        
        
        
        try(Connection con = getCon() ; PreparedStatement ps = con.prepareStatement(sql)){
            
            con.setAutoCommit(false);
            Savepoint save = con.setSavepoint();
            
          try{  ps.setLong(1, contract.getContractPeriod().getContractPeriodId());
            ps.setString(2,contract.getDecisionDate().toString());
            ps.setString(3,contract.getDecision().toString());
            ps.setBoolean(4,contract.isDeleted());
            
            if(ps.executeUpdate() > 0){
                con.commit();
                return Optional.of(contract);
                
            }
            else {
                con.rollback(save);
            }
          
          }catch(Exception e){
              System.out.println("Error while updating a contract, Try again later");
          }
            
            
        }
        return Optional.empty();
    }

    
}
