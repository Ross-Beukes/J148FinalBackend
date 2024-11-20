package com.j148.backend.contract.service;

import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.contract.model.Contract;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.user.model.User;

public interface ContractService {
    Contract offerContract(User user, AptitudeTest aptitudeTest, FileEntity idFile, FileEntity matricCertificateFile) throws Exception;
}
