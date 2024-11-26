
package com.j148.backend.contract.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contract.model.Contract;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

//Author : Tshireletso
@ApplicationScoped
public class ContractRepoImpl implements ContractRepo {

    private static final Logger LOG = Logger.getLogger(ContractRepoImpl.class.getName());

    @Inject
    private DBConfig DBConfig;


    @Override
    public Optional<Contract> createContract(Contract contract) throws SQLException {
        String sql = "INSERT INTO contract(contract_period_id,user_id,offer_date,expiration_date) "
                + " VALUES(?,?,?,?) ";

        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            try {

                ps.setLong(1, contract.getContractPeriod().getContractPeriodId());
                ps.setLong(2, contract.getUser().getUserId());
                ps.setString(3, String.valueOf(contract.getOfferDate()));
                ps.setString(4, String.valueOf(contract.getExpirationDate()));

                if (ps.executeUpdate() > 0) {

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            contract.setContractId(rs.getLong(1));
                            return Optional.of(contract);
                        }

                    }
                }


            }catch (Exception e){
                LOG.log(Level.SEVERE, "There was an error creating the contract", e);
                throw e;
            }

        }

        return Optional.empty();

    }

    @Override
    public Optional<Contract> findContract(long contractId) throws SQLException {
//         String sql = "SELECT * FROM contract WHERE contract_id = ? ";
//
//         try(Connection con = DBConfig.getCon() ; PreparedStatement ps = con.prepareStatement(sql)){
//
//             ps.setLong(1,contractId);
//
//             try(ResultSet rs = ps.executeQuery()){
//               if(rs.next()){
//                 ContractPeriod c = cpri.findContract(rs.getLong(2)).get() ;
//                 User user = new User();
//                 user.setUserId(rs.getLong(3));
//                 user = uri.retreiveUserFromUserID(user).get();
//
//                 Contract contract = Contract.builder().
//                         contractId(contractId)
//                         .contractPeriod(c)
//                         .offerDate(rs.getDate(4).toLocalDate())
//                         .decisionDate(rs.getDate(5).toLocalDate())
//                         .expirationDate(rs.getDate(6).toLocalDate())
//                         .user(user)
//                         .decision(Contract.Decision.valueOf(rs.getString(7)))
//                         .isDeleted(rs.getBoolean(8))
//                         .build();
//
//                 return Optional.of(contract);
//               }
//             }
//         }
//
//        return Optional.empty();
        return Optional.empty();
    }

    @Override
    public Optional<Contract> updateContract(Contract contract) throws SQLException {
        String sql = "UPDATE contract SET contractor_period_id = ? decision_date = ? decision = ? deleted = ? "
                + "WHERE contract_id = ?";


        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, contract.getContractPeriod().getContractPeriodId());
            ps.setString(2, contract.getDecisionDate().toString());
            ps.setString(3, contract.getDecision().toString());
            ps.setBoolean(4, contract.isDeleted());

            if (ps.executeUpdate() > 0) {
                return Optional.of(contract);

            }


        }
        return Optional.empty();
    }


}
