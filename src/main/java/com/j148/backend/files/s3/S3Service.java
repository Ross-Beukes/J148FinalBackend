package com.j148.backend.files.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.files.repo.FileEntityRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

@ApplicationScoped
public class S3Service {

    @Inject
    private S3Repo S3Repo;

    @Inject
    private FileEntityRepo fileEntityRepo;

    private static final String BUCKET_NAME = "vzapbucket";


    @Transactional(rollbackOn = {Exception.class
            })
    public void uploadFile(InputStream fileStream,FileEntity fileEntity) throws SQLException {
        FileEntity returnedFileEntity = fileEntityRepo.saveFile(fileEntity).get();
        S3Repo.uploadFile(BUCKET_NAME, fileStream, returnedFileEntity);
    }

    public InputStream downloadFile(String key) {
        return S3Repo.downloadFile(BUCKET_NAME, key);
    }
}
