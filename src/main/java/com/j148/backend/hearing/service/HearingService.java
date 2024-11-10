package com.j148.backend.hearing.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;

public interface HearingService {
    Hearing rescheduleHearing(Hearing hearing, Contractor contractor) throws Exception;
}
