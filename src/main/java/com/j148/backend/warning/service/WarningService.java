package com.j148.backend.warning.service;

import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.warning.model.Warning;

public interface WarningService {
    Warning appealWarning(Warning warning, Contractor contractor) throws Exception;
}
