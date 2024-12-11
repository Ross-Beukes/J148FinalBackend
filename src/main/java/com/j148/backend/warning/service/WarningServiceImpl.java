package com.j148.backend.warning.service;

import com.j148.backend.Exceptions.ContractorNotFoundException;
import com.j148.backend.Exceptions.WarningNotFoundException;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.notification.EmailSender;
import com.j148.backend.warning.model.Warning;
import com.j148.backend.warning.repo.WarningRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author glenl
 */
@ApplicationScoped
public class WarningServiceImpl implements WarningService {

    @Inject
    private WarningRepo warningRepo;
    @Inject
    private ContractorRepo contractorRepo;
    @Inject
    private EmailSender emailSender;


    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public Warning lateComingWarning(Contractor contractor) throws SQLException, Exception {
        if (contractor != null) {
            if (contractor.getContractorId() != null) {

                try {
                    emailSender.sendNotification(contractor.getUser().getEmail()
                            ,"Dear " + contractor.getUser().getName()
                                    + "\n\nThis message serves to inform you that you have received a warning for checking in LATE."
                                    +"Repeated offenses will result in a disciplinary hearing and are documented as part of your performance. To avoid this please ensure you check-in before '8:30'."
                                    +"\nKind regards," + "\nAdmin"
                            , "Late warning Issued");
                } catch (MessagingException ex) {
                    Logger.getLogger(WarningServiceImpl.class.getName()).log(Level.SEVERE, "late coming warning message error, Error sending e-mail informing a contractor they received a warning", ex);
                }

                return warningRepo.createLateWarning(contractor).orElseThrow(() -> new RuntimeException("Warning could not be issued"));

            } else {
                throw new IllegalArgumentException("Contract ID is null");
            }
        } else {
            throw new IllegalArgumentException("Contractor is null");
        }
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public Warning absentWarning(Contractor contractor) throws SQLException, Exception {
        if (contractor != null) {
            if (contractor.getContractorId() != null) {
                return warningRepo.createAbsentWarning(contractor).orElseThrow(() -> new RuntimeException("Warning could not be issued"));
            } else {
                throw new IllegalArgumentException("Contract ID is null");
            }
        } else {
            throw new IllegalArgumentException("Contractor is null");
        }
    }

    @Override
    public Warning save(Warning warning) throws SQLException, WarningNotFoundException, ContractorNotFoundException {
        return null;
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public Warning appealWarning(Warning warning, Contractor contractor) throws Exception {

        if (warning == null) {
            throw new IllegalArgumentException("Warning is null");
        }
        if (contractor == null) {
            throw new IllegalArgumentException("Contractor is null");
        }

        if (warning.getWarningId() == null) {
            throw new IllegalArgumentException("Warning id is null");
        }

        if (contractor.getContractorId() == null) {
            throw new IllegalArgumentException("Contractor id is null");
        }

        if (contractorRepo.findById(contractor).isEmpty()) {
            throw new IllegalArgumentException("Could not find contractor");
        }

        if (warningRepo.findById(warning).isEmpty()) {
            throw new IllegalArgumentException("Could not find warning");
        }
        if (warning.getWarningId() != null)
            warning.setState(Warning.WarningState.APPEALED);
        return warningRepo.updateState(warning)
                .orElseThrow(() -> new RuntimeException("Failed to appeal warning"));

    }

    @Override
    public Warning findById(Warning warning) throws SQLException {
        return null;
    }

    @Override
    public List<Warning> findByContractor(Contractor contractor) throws SQLException {
        return null;
    }

    @Override
    public List<Warning> findAllActiveByContractor(Contractor contractor) throws Exception {
        if (contractor != null) {
            System.out.println(warningRepo.findAllActiveByContractor(contractor).orElseThrow(() -> new Exception("No Warnings found.")));
            return warningRepo.findAllActiveByContractor(contractor).orElseThrow(() -> new Exception("No Warnings found."));
        }
        return null;
    }

    @Override
    public List<Warning> findAppealedByContractor(Contractor contractor) throws SQLException {
        return null;
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public Warning updateState(Warning warning) throws Exception {

        if (warning.getWarningId() != null && warning.getState() != null) {
            Warning returnedWarning =
                    warningRepo.updateState(warning).orElseThrow(() -> new Exception("The returned warning is null while trying to update the state" ));

            try {
                emailSender.sendNotification(warning.getContractor().getUser().getEmail()
                        , "Dear " + warning.getContractor().getUser().getName()
                                + "\n\nThis message serves to inform you that your appealed warning for the day : "
                                + warning.getDateIssue().toString()
                                + "has been reviewed and it has been made "
                                + warning.getState().toString()
                                + "\nKind regards," + "\nAdmin"
                        , "Decision on your appealed warning");
            } catch (MessagingException ex) {
                Logger.getLogger(WarningServiceImpl.class.getName()).
                        log(Level.SEVERE, "Update state messaging error, Error sending e-mail for updating a contractor on their warning status", ex);
            }

            return  returnedWarning;
        }else{
            throw new IllegalArgumentException("Failed to update the warning state because of null values for id or state");
        }
    }


    @Override
    public Warning createLateWarning(Contractor contractor) throws SQLException {
        return null;
    }

    @Override
    public List<Warning> findWarningsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        return null;
    }

    @Override
    public List<Warning> findFinalWarningsByContractor(Contractor contractor) throws SQLException {
        return null;
    }

    @Override
    public Optional<Long> countActiveWarningsByContractor(Contractor contractor) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<Warning> findWarningsByReason(Warning warning) throws SQLException {
        return null;
    }

    @Override
    public Boolean existsByContractorAndDateIssue(Contractor contractor, LocalDateTime dateIssue) throws SQLException {
        return null;
    }


    @Override
    public ArrayList<Warning> findAllAppealedWarnings() throws SQLException {

        return warningRepo.findAllAppealedWarnings();

    }
}

