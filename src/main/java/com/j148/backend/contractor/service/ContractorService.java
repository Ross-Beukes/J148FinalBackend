package com.j148.backend.contractor.service;


import com.j148.backend.contractor.model.Contractor;

public interface ContractorService {


    Contractor changeContractorStatus(Contractor contractor) throws Exception;
}
