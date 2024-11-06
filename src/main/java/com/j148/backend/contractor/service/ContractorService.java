package com.j148.backend.contractor.service;

import com.j148.backend.contractor.model.Contractor;

import java.sql.SQLException;
import java.util.Optional;

public interface ContractorService {
    Optional<Contractor> update(Contractor contractor) throws SQLException;
}
