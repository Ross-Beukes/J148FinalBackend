package com.j148.backend.files.repo;

import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.user.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Repository interface for managing file operations in the HRMS system.
 * Handles file storage, retrieval, and management both in the database and filesystem.
 */
public interface FileEntityRepo {


    Optional<FileEntity> saveFile(FileEntity fileEntity) throws SQLException;

    Optional<FileEntity> findById(FileEntity fileEntity) throws SQLException;


    List<FileEntity> findAllPendingVerifications() throws SQLException;

    List<FileEntity> findFilesByContractPeriod(ContractPeriod contractPeriod) throws SQLException;

    List<FileEntity> getApprovedTimesheetsByYear(Contractor contractor, int year) throws SQLException;

    List<FileEntity> findValidFilesByUserIdAndCategory(User user, FileEntity fileEntity) throws SQLException;

    Optional<FileEntity> findFileByUserIdAndCategory(User user, FileEntity fileEntity) throws SQLException;

    Optional<FileEntity> fileVerification(FileEntity fileEntity) throws SQLException;

    ArrayList<FileEntity> retrieveFilesWithUsers()throws SQLException;

}