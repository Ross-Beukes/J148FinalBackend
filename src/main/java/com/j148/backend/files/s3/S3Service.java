package com.j148.backend.files.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.files.repo.FileEntityRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.sql.SQLException;

@ApplicationScoped
public class S3Service {

    @Inject
    private S3Repo S3Repo;

    @Inject
    private FileEntityRepo fileEntityRepo;

    private static final String BUCKET_NAME = "vzapbucket";


    @Transactional(rollbackOn = {SQLException.class, AmazonS3Exception.class,
            AmazonServiceException.class,
            SdkClientException.class})
    public FileEntity uploadFile(InputStream fileStream, FileEntity fileEntity) throws RuntimeException, SQLException {
        try {
            FileEntity returnedFileEntity = fileEntityRepo.saveFile(fileEntity).get();
            S3Repo.uploadFile(BUCKET_NAME, fileStream, returnedFileEntity);
            return returnedFileEntity;
        } catch (Exception e) {
            // Convert RuntimeException to one of the rollbackOn exceptions
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    public InputStream downloadFile(String key) throws RuntimeException {
        try {
            return S3Repo.downloadFile(BUCKET_NAME, key);
        }catch (Exception e){
            throw new RuntimeException("S3 download failed", e);
        }
    }
}
