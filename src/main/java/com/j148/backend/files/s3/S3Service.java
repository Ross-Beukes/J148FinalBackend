package com.j148.backend.files.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.files.repo.FileEntityRepo;
import com.j148.backend.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class S3Service {

    @Inject
    private S3Repo S3Repo;

    @Inject
    private FileEntityRepo fileEntityRepo;

    private static final String BUCKET_NAME = "vzapbucket";


    @Transactional(rollbackOn = {SQLException.class, AmazonS3Exception.class,
            AmazonServiceException.class,
            SdkClientException.class,})
    public FileEntity uploadFile(InputStream fileStream,FileEntity fileEntity) throws SQLException {
        FileEntity returnedFileEntity = fileEntityRepo.saveFile(fileEntity).get();
        S3Repo.uploadFile(BUCKET_NAME, fileStream, returnedFileEntity);
        return returnedFileEntity;
    }

    public InputStream downloadFile(String key) {
        return S3Repo.downloadFile(BUCKET_NAME, key);
    }
    
    public List<FileEntity> findAllPendingVerifications() throws Exception {
        try {
            List<FileEntity> pendingFiles = fileEntityRepo.findAllPendingVerifications();
            if (pendingFiles.isEmpty()) {
                return new ArrayList<>();
            }
            return pendingFiles;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving pending verifications", e);
        }
    }
    
    public List<FileEntity> findFilesByContractPeriod(ContractPeriod contractPeriod) throws Exception {
        if (contractPeriod == null || contractPeriod.getContractPeriodId() == null) {
            throw new IllegalArgumentException("Contract period cannot be null");
        }

        try {
            List<FileEntity> periodFiles = fileEntityRepo.findFilesByContractPeriod(contractPeriod);
            if (periodFiles.isEmpty()) {
                return new ArrayList<>();
            }
            return periodFiles;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving files for contract period", e);
        }
    }

    
    public List<FileEntity> getApprovedTimesheetsByYear(Contractor contractor, int year) throws Exception {
        if (contractor == null || contractor.getUser() == null) {
            throw new IllegalArgumentException("Contractor or user cannot be null");
        }

        if (year < 1900 || year > LocalDateTime.now().getYear()) {
            throw new IllegalArgumentException("Invalid year provided");
        }

        try {
            List<FileEntity> timesheets = fileEntityRepo.getApprovedTimesheetsByYear(contractor, year);
            if (timesheets.isEmpty()) {
                return new ArrayList<>();
            }
            return timesheets;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving approved timesheets", e);
        }
    }
    
    public List<FileEntity> findValidFilesByUserIdAndCategory(User user, FileEntity fileEntity) throws Exception {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (fileEntity == null) {
            throw new IllegalArgumentException("File entity cannot be null");
        }

        try {
            List<FileEntity> validFiles = fileEntityRepo.findValidFilesByUserIdAndCategory(user, fileEntity);
            if (validFiles.isEmpty()) {
                return new ArrayList<>();
            }
            return validFiles;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving valid files", e);
        }
    }
}