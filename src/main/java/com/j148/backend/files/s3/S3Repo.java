package com.j148.backend.files.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;

@ApplicationScoped
public class S3Repo {

    @Inject
    private AmazonS3 amazonS3;

    public void uploadFile(String bucketName, String key, InputStream fileStream, long fileSize, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(contentType);

        amazonS3.putObject(bucketName, key, fileStream, metadata);
    }

    public InputStream downloadFile(String bucketName, String key) {
        return amazonS3.getObject(new GetObjectRequest(bucketName, key)).getObjectContent();
    }
}
