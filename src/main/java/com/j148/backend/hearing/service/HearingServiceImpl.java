package com.j148.backend.hearing.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.repo.ContractorRepo;
import com.j148.backend.contractor.repo.ContractorRepoImpl;
import com.j148.backend.hearing.model.Hearing;
import com.j148.backend.hearing.repo.HearingRepo;
import com.j148.backend.hearing.repo.HearingRepoImpl;

import java.time.LocalDateTime;

public class HearingServiceImpl implements HearingService {

    private final HearingRepo hearingRepo = new HearingRepoImpl();
    private final ContractorRepo contractorRepo = new ContractorRepoImpl();

    @Override
    public Hearing rescheduleHearing(Hearing hearing, Contractor contractor) throws Exception {

        if (hearing == null){
            throw new IllegalArgumentException("User is null");
        }
        if (contractor == null){
            throw new IllegalArgumentException("Contractor is null");
        }

        if (hearing.getHearingsId() == null || hearing.getScheduleDate() == null ){
            throw new IllegalArgumentException("Hearing schedule date or id is null");
        }

        if (contractor.getContractorId() == null){
            throw new IllegalArgumentException("Contractor id is null");
        }

        if (contractorRepo.findById(contractor.getContractorId()).isEmpty()){
            throw new IllegalArgumentException("Could not find contractor");
        }

        if (hearingRepo.getHearing(hearing).isEmpty()){
            throw new IllegalArgumentException("Could not find hearing");
        }

        if (hearing.getScheduleDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Hearing must be in the future");
        }

        return hearingRepo.updateHearing(hearing)
                .orElseThrow(() -> new Exception("Failed to reschedule hearing"));

    }
}
