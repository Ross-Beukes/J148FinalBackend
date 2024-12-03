package com.j148.backend.files.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.j148.backend.files.model.FileEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;

@ApplicationScoped
public class S3Repo {

    @Inject
    private AmazonS3 amazonS3;

    public void uploadFile(String bucketName, InputStream fileStream, FileEntity fileEntity) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileEntity.getFileSize());
        metadata.setContentType(fileEntity.getFileType());

        amazonS3.putObject(bucketName, fileEntity.getFileId().toString(), fileStream, metadata);
    }

    public InputStream downloadFile(String bucketName, String key) {
        return amazonS3.getObject(new GetObjectRequest(bucketName, key)).getObjectContent();
    }

    public void deleteFile(String bucketName, String key) {
        try {
            amazonS3.deleteObject(bucketName, key);
        } catch (AmazonS3Exception e) {
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
        }
    }
}