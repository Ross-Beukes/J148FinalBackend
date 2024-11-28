package com.j148.backend.files.s3;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
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

    public void uploadFile(String bucketName, InputStream fileStream, FileEntity fileEntity) throws RuntimeException {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileEntity.getFileSize());
            metadata.setContentType(fileEntity.getFileType());
            amazonS3.putObject(bucketName, fileEntity.getFileId().toString(), fileStream, metadata);
        } catch (Exception e) {
            throw new AmazonServiceException("Failed to upload to S3", e);
        }
    }

    public InputStream downloadFile(String bucketName, String key)  throws RuntimeException {
       try {
            return amazonS3.getObject(new GetObjectRequest(bucketName, key)).getObjectContent();
        }catch(Exception e){
           throw new AmazonServiceException("Failed to download from S3", e);
       }
       }
}
