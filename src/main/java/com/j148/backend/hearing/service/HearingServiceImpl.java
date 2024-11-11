package com.j148.backend.hearing.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.contractor.repo.ContractorRepoImpl;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.hearing.repo.HearingRepo;
import com.j148.backend.hearing.repo.HearingRepoImpl;
import com.j148.backend.warning.repo.WarningRepoImpl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HearingServiceImpl implements HearingService {

    private final HearingRepo hearingRepo = new HearingRepoImpl();
    private final ContractorRepo contractorRepo = new ContractorRepoImpl();

    @Override
    public LocalDateTime scheduleHearing() throws Exception {
        // get one week to the current date and time
        LocalDateTime hearingDate = LocalDateTime.now().plusWeeks(1);

        return hearingDate;
    }

    @Override
    public Hearing IssueHearing(Contractor contractor) throws Exception {

        if (contractor != null) {
            int hearingCount = 0;
            long warningCount = 0l;
            HearingRepoImpl hri = new HearingRepoImpl();

            //Obtain count based on a Contractors amount of hearings already
            try {
                hearingCount = hri.findContractorHearingHistory(contractor).size();
            } catch (SQLException ex) {
                Logger.getLogger(HearingServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            WarningRepoImpl wri = new WarningRepoImpl();
            //Obtain count based on a Contractors amount of active warnings
            try {
                warningCount = wri.countActiveWarningsByContractor(contractor).get();
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
                    return hearing;
                }

            }
        }
        return null;
    }

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

        if (contractorRepo.findById(contractor.getContractorId()).isEmpty()) {
            throw new IllegalArgumentException("Could not find contractor");
        }

        if (hearingRepo.getHearing(hearing).isEmpty()) {
            throw new IllegalArgumentException("Could not find hearing");
        }

        if (hearing.getScheduleDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Hearing must be in the future");
        }

        return hearingRepo.updateHearing(hearing)
                .orElseThrow(() -> new Exception("Failed to reschedule hearing"));


    }
}





