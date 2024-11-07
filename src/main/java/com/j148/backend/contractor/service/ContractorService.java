package com.j148.backend.contractor.service;

import com.j148.backend.contractor.model.Contractor;

public interface ContractorService {
 /**
     * Updates the information of an existing contractor.
     *
     * @param contractor The Contractor object containing updated information.
     * @return The updated Contractor object after changes have been saved.
     * @throws Exception if the contractor could not be updated due to any underlying issues, 
     *                   such as database errors or invalid contractor data.
     */   
 public Contractor updateContractor (Contractor contractor) throws Exception;
}
