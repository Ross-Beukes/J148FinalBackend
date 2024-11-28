package com.j148.backend.files.s3;

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


    @Transactional(rollbackOn = {Exception.class})
    public FileEntity uploadFile(InputStream fileStream, FileEntity fileEntity) throws RuntimeException, SQLException {
        try {

            S3Repo.uploadFile(BUCKET_NAME, fileStream, fileEntity);
            FileEntity returnedFileEntity = fileEntityRepo.saveFile(fileEntity).get();
            return returnedFileEntity;
        } catch (Exception e) {
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    public InputStream downloadFile(String key) throws RuntimeException {
        try {
            return S3Repo.downloadFile(BUCKET_NAME, key);
        } catch (Exception e) {
            throw new RuntimeException("S3 download failed", e);
        }
    }
}
