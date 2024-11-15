package com.j148.backend.files.repo;

import com.j148.backend.files.model.FileEntity;
import com.j148.backend.user.model.User;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.swing.plaf.synth.Region;

/**
 * Repository interface for managing file operations in the HRMS system.
 * Handles file storage, retrieval, and management both in the database and filesystem.
 */
public interface FileEntityRepo {

    /**
     * Saves a file entity to the database without physical file handling.
     *
     * @param fileEntity the file entity to be saved
     * @return Optional containing the saved FileEntity with generated ID if successful, empty Optional otherwise
     */
    Optional<FileEntity> saveFile(FileEntity fileEntity);

    /**
     * Saves both the physical file and its metadata in the database.
     *
     * @param filePart the file content from HTTP multipart request
     * @param user the user who is uploading the file
     * @param category the category of the file being uploaded
     * @return Optional containing the saved FileEntity if successful, empty Optional otherwise
     * @throws SQLException if database operation fails
     */
    Optional<FileEntity> save(Part filePart, User user, FileEntity.Category category) throws SQLException;

    /**
     * Deletes a file from both the filesystem and database.
     *
     * @param fileEntity the file entity to be deleted
     * @return Optional containing true if deletion was successful, false otherwise
     * @throws SQLException if database operation fails
     */
    Optional<Boolean> deleteFile(FileEntity fileEntity) throws SQLException;

    /**
     * Finds a file entity by its ID with full user information.
     *
     * @param fileEntity the file entity containing the ID to search for
     * @return Optional containing the found FileEntity with user details if exists, empty Optional otherwise
     * @throws SQLException if database operation fails
     */
    Optional<FileEntity> findById(FileEntity fileEntity) throws SQLException;

    /**
     * Downloads the physical file content from the file system.
     *
     * @param file the file entity to download
     * @return Optional containing the file bytes if found, empty Optional otherwise
     * @throws SQLException if database operation fails
     * @throws IOException if file reading fails
     */
    Optional<byte[]> downloadFile(FileEntity file) throws SQLException, IOException;

    /**
     * Finds all files with a specific ID.
     *
     * @param fileId the ID to search for
     * @return List of FileEntity objects matching the ID
     */
    List<FileEntity> findById(long fileId);

    /**
     * Finds all files of a specific category.
     *
     * @param category the category to search for
     * @return List of FileEntity objects matching the category
     */
    List<FileEntity> findByCategory(FileEntity.Category category);

    /**
     * Finds all files with a specific verification status.
     *
     * @param verified the verification status to search for
     * @return List of FileEntity objects matching the verification status
     */
    List<FileEntity> findByStatus(FileEntity.Verified verified);

    /**
     * Retrieves all files in the system.
     *
     * @return List of all FileEntity objects in the database
     */
    List<FileEntity> getAllFiles();
    
    /**
     * Uploads a file from your local machine to the Amazon simple storage service
     * file storage 
     * 
     * @param fileEntity     
     * @return     
     * @throws java.sql.SQLException     
     */
    
    Optional<FileEntity> UploadFileS3(FileEntity fileEntity) throws SQLException;
    
    /**
     * Downloads a file from the Amazon simple storage service file storage to
     * your local machine
     *     
     * @param fileEntity
     * @return     
     * @throws java.sql.SQLException     
     */
    
    Optional<FileEntity> downloadFileS3(FileEntity fileEntity) throws SQLException;
    
    
    
    
}