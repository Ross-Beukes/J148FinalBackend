package com.j148.backend.contract.service;

import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.contract.model.Contract;
import com.j148.backend.contract.repo.ContractRepo;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contract_period.service.ContractPeriodService;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.notification.EmailSender;
import com.j148.backend.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
public class ContractServiceImpl implements ContractService {

    @Inject
    ContractRepo contractRepo;
    @Inject
    ContractPeriodService contractPeriodService;
    @Inject
    EmailSender emailSender;


    @Transactional(dontRollbackOn = { IllegalArgumentException.class, IllegalStateException.class},rollbackOn = {SQLException.class})
    @Override
    public Contract offerContract(User user, AptitudeTest aptitudeTest, FileEntity idFile, FileEntity matricCertificateFile) throws Exception {
        validateAllOfferAttributes(user, aptitudeTest, idFile, matricCertificateFile);
        if (aptitudeTest.getTestMark() >= 65
                && idFile.getVerified() == FileEntity.Verified.APPROVED
                && matricCertificateFile.getVerified() == FileEntity.Verified.APPROVED) {
            Contract contract;
            ContractPeriod contractPeriod = contractPeriodService.getNextContractPeriod();
            if (LocalDate.now().getDayOfYear() - contractPeriod.getStartDate().getDayOfYear() <= 14) {
                contract = Contract.builder().contractPeriod(contractPeriod).offerDate(LocalDate.now())
                        .expirationDate(contractPeriod.getStartDate().minusDays(1)).user(user).build();
            } else {
                contract = Contract.builder().contractPeriod(contractPeriod).offerDate(LocalDate.now())
                        .expirationDate(LocalDate.now().plusDays(14)).user(user).build();
            }
            validateContractOffer(contract);
            emailSender.sendNotification(user.getEmail(), "Contract : " + contract.toString(), "Contract offer : " + user.getName() + " " + user.getSurname());
            return contractRepo.createContract(contract).orElseThrow(()
                    -> new RuntimeException("Could not offer contract (create new contract) due to an error"));
        } else {
            throw new IllegalArgumentException("Aptitude test mark too low to offer user contract or a document has not been approved");
        }
    }

    @Override
    public Contract findActiveContractOffer(User user) throws Exception {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User cannot be null and must have an ID");
        }

        if (user.getRole() != User.Role.APPLICANT) {
            throw new IllegalArgumentException("Contract offers can only be checked for applicants");
        }

        Optional<Contract> contractOpt = contractRepo.findActiveContractOffer(user);
        return contractOpt.orElse(null); // Returns null if no active contract offer exists
    }

    private void validateContractOffer(Contract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null when offered");
        }
    }
    
    private void validateAllOfferAttributes(User user, AptitudeTest aptitudeTest, FileEntity idFile, FileEntity matricCertificateFile){
        if (user == null) {
            throw new NullPointerException("User cannot be null when creating contract offer");
        }
        if (aptitudeTest == null) {
            throw new NullPointerException("Aptitude test cannot benull when creating contract offer");
        }
        if (idFile == null) {
            throw new NullPointerException("ID File cannot be null when creating contract offer");
        }
        if (matricCertificateFile == null) {
            throw new NullPointerException("Matric certificate file cannot be null when creating contract offer");
        }
        if (idFile.getVerified() == FileEntity.Verified.WAITING) {
            throw new IllegalStateException("ID File has not been verified");
        }
        if (matricCertificateFile.getVerified() == FileEntity.Verified.WAITING) {
            throw new IllegalStateException("Matric cerificate file has not been verified");
        }
        if (idFile.getVerified() == FileEntity.Verified.REJECTED) {
            throw new IllegalStateException("ID file was rejected");
        }
        if (matricCertificateFile.getVerified() == FileEntity.Verified.REJECTED) {
            throw new IllegalStateException("Matric cerificate file was rejected");
        }
        if (aptitudeTest.getTestMark() < 65) {
            throw new IllegalArgumentException("Aptitude mark below 65%, does not qualify for contract offer");
        }
    }

}
