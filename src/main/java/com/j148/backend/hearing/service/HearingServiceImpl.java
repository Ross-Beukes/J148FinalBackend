package com.j148.backend.hearing.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.hearing.repo.HearingRepo;

import java.sql.SQLException;

import com.j148.backend.warning.repo.WarningRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class HearingServiceImpl implements HearingService {

    @Inject
    private HearingRepo hearingRepo;
    @Inject
    private ContractorRepo contractorRepo;
    @Inject
    private WarningRepo warningRepo;


    @Override
    public LocalDateTime scheduleHearing() throws Exception {
        // get one week to the current date and time
        LocalDateTime hearingDate = LocalDateTime.now().plusWeeks(1);

        return hearingDate;
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public Hearing IssueHearing(Contractor contractor) throws Exception {

        if (contractor != null) {
            int hearingCount = 0;
            long warningCount = 0l;

            //Obtain count based on a Contractors amount of hearings already
            try {
                hearingCount = hearingRepo.findContractorHearingHistory(contractor).size();
            } catch (SQLException ex) {
                Logger.getLogger(HearingServiceImpl.class.getName()).log(Level.SEVERE, "Error while viewing disciplinary hearing history", ex);

            }
            //Obtain count based on a Contractors amount of active warnings
            try {
                warningCount = warningRepo.countActiveWarningsByContractor(contractor).get();
            } catch (SQLException ex) {
                Logger.getLogger(HearingServiceImpl.class.getName()).log(Level.SEVERE, "Error while viewing warning history", ex);
            }

            if (warningCount % 3 == 0 && warningCount > 0) {

                if (hearingCount * 3 != warningCount) {

                    Hearing hearing = Hearing.builder()
                            .scheduleDate(scheduleHearing())
                            .hearingsId(0L)
                            .contractor(contractor)
                            .reason("Contractor has received three or more warnings for being late or absent")
                            .outcome(Hearing.Outcome.NULL)
                            .build();

                    //Add a new Disciplinary hearing to database
                    return hearingRepo.createHearing(hearing).orElseThrow(() -> new RuntimeException("Error, A disciplinary hearing was not issued to the contractor"));


                }

            }
        }
        return null;
    }

    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public Hearing rescheduleHearing(Hearing hearing, Contractor contractor) throws Exception {

        if (hearing == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (contractor == null) {
            throw new IllegalArgumentException("Contractor is null");
        }

        if (hearing.getHearingsId() == null || hearing.getScheduleDate() == null) {
            throw new IllegalArgumentException("Hearing schedule date or id is null");
        }

        if (contractor.getContractorId() == null) {
            throw new IllegalArgumentException("Contractor id is null");
        }

        if (contractorRepo.findById(contractor).isEmpty()) {
            throw new IllegalArgumentException("Could not find contractor");
        }

        if (hearingRepo.getHearing(hearing).isEmpty()) {
            throw new IllegalArgumentException("Could not find hearing");
        }

        if (hearing.getScheduleDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Hearing must be in the future");
        }

        return hearingRepo.updateHearing(hearing)
                .orElseThrow(() -> new RuntimeException("Failed to reschedule hearing"));
    }
    
    @Transactional(dontRollbackOn = {IllegalArgumentException.class, IllegalStateException.class}, rollbackOn = {SQLException.class})
    @Override
    public List<Hearing> getAllHearings() throws Exception {
        return hearingRepo.findAllHearings();
    }

    @Override
    public Hearing updateHearing(Hearing hearing) throws Exception {
        if (hearing == null) {
            throw new IllegalArgumentException("User is null");
        }

        if (hearing.getHearingsId() == null || hearing.getScheduleDate() == null) {
            throw new IllegalArgumentException("Hearing schedule date or id is null");
        }

        if (hearingRepo.getHearing(hearing).isEmpty()) {
            throw new IllegalArgumentException("Could not find hearing");
        }
        
        if (hearing.getOutcome().equals(Hearing.Outcome.SUSPENDED)) {
            Contractor contractor = hearing.getContractor();
            contractor.setStatus(Contractor.Status.SUSPENDED);
            Contractor updatedContractor = contractorRepo.updateStatus(contractor).get();
        }

        return hearingRepo.updateHearing(hearing).orElseThrow(() -> new RuntimeException("Failed to reschedule hearing"));
    }
}
