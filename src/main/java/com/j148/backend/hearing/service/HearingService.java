package com.j148.backend.hearing.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import java.time.LocalDateTime;

public interface HearingService {

    /**
     * The issue hearing method will create a new disciplinary hearing based on
     * wether a Contractor has more than 3 or more warnings. One Hearing is
     * issued per Three Warnings
     *
     @param contractor
      * @return Hearing
     */
    public Hearing IssueHearing(Contractor contractor) throws Exception;


    /**
     * The method generates a date and time exactly one week after someone is
     * issued a disciplinary hearing
     *
     *
     *
     *@return LocalDateTime
     */
    public LocalDateTime scheduleHearing() throws Exception ;

    Hearing rescheduleHearing(Hearing hearing, Contractor contractor) throws Exception;
}
