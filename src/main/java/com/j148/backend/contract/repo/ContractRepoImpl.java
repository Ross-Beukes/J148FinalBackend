
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
import java.util.logging.Level;
import java.util.logging.Logger;

//Author : Tshireletso

public class ContractRepoImpl extends DBConfig  implements ContractRepo{
    
    private static final Logger LOG = Logger.getLogger(ContractRepoImpl.class.getName());
    

    @Override
    public Optional<Contract> createContract(Contract contract) throws SQLException {
        String sql = "INSERT INTO contract(contract_period_id,user_id,offer_date,expiration_date) "
                + " VALUES(?,?,?,?) ";
        
        try(Connection con = getCon() ; PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
        con.setAutoCommit(false);
        Savepoint save = con.setSavepoint();
        
        try{
            
            ps.setLong(1, contract.getContractPeriod().getContractPeriodId());
            ps.setLong(2,contract.getUser().getUserId());
            ps.setString(3,String.valueOf(contract.getOfferDate()));
            ps.setString(4,String.valueOf(contract.getExpirationDate()));
            
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
            LOG.log(Level.SEVERE, "\"Error while creating a new contract\"", e);

        }
            
        }
        
        return Optional.empty();
       
    }
    @Override
    public Optional<Contract> findContract(long contractId) throws SQLException{
        String sql = """
                SELECT c.*, cp.*, u.*
                FROM contract c  
                JOIN user u ON c.user_id = u.user_id 
                JOIN contractor_period cp ON c.contractor_period_id = cp.contractor_period_id
                WHERE c.contract = ?
                """;

       try(Connection con = getCon() ; PreparedStatement ps = con.prepareStatement(sql)){

             ps.setLong(1,contractId);

             try(ResultSet rs = ps.executeQuery()){
               if(rs.next()){
                
                
                 Contract contract = Contract.builder().
                         contractId(contractId)
                         .contractPeriod(mapContractPeriodFromResultSet(rs))
                         .offerDate(rs.getDate(4).toLocalDate())
                         .decisionDate(rs.getDate(5).toLocalDate())
                         .expirationDate(rs.getDate(6).toLocalDate())
                         .user(mapUserFromResultSet(rs))
                         .decision(Contract.Decision.valueOf(rs.getString(7)))
                         .isDeleted(rs.getBoolean(8))
                         .build();

                 return Optional.of(contract);
               }             }
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
               LOG.log(Level.SEVERE, "Error while updating a contract, Try again later", e);
               throw e;
          }
            
            
        }
        return Optional.empty();
    }

        private ContractPeriod mapContractPeriodFromResultSet(ResultSet rs) throws SQLException {
        return ContractPeriod.builder()
                .contractPeriodId(rs.getLong("contractor_period_id"))
                .name(rs.getString("name"))
                .startDate(rs.getDate("start_date").toLocalDate())
                .endDate(rs.getDate("end_date").toLocalDate())
                .build();
    }

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        return User.builder()
                .userId(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .surname(rs.getString("surname"))
                .email(rs.getString("email"))
                .gender(rs.getString("gender"))
                .idNumber(rs.getString("id_number"))
                .role(User.Role.valueOf(rs.getString("role")))
                .race(rs.getString("race"))
                .location(rs.getString("location"))
                .age(rs.getInt("age"))
                .build();
    }
    
}
