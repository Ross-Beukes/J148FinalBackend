package com.j148.backend.files.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.files.repo.FileEntityRepo;
import com.j148.backend.user.model.User;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.UserTransaction;

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

    @Resource
    private UserTransaction userTransaction;


    private static final String BUCKET_NAME = "vzapbucket";


    public FileEntity uploadFile(InputStream fileStream, FileEntity fileEntity) throws SQLException {
        FileEntity returnedFileEntity = null;

        try {
            // Explicitly begin the transaction
            userTransaction.begin();

            // Perform the database operation
            returnedFileEntity = fileEntityRepo.saveFile(fileEntity).get();

            // Attempt to upload the file to S3
            S3Repo.uploadFile(BUCKET_NAME, fileStream, returnedFileEntity);

            // If we reach this point without exceptions, commit the transaction
            userTransaction.commit();

        } catch (SQLException | SdkClientException e) {
            // If any of our specified exceptions occur, roll back the transaction
            try {
                userTransaction.rollback();
            } catch (Exception rollbackException) {
                // Log rollback failure - this is a serious issue
                throw new RuntimeException("Transaction rollback failed", rollbackException);
            }
            // Re-throw the original exception
            throw e;
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            try {
                userTransaction.rollback();
            } catch (Exception rollbackException) {
                throw new RuntimeException("Transaction rollback failed", rollbackException);
            }
            throw new RuntimeException("Error uploading file", e);
        }

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