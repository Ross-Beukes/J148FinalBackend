package com.j148.backend.files.s3;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;

@ApplicationScoped
public class S3Service {

    @Inject
    private S3Repo S3Repo;

    private static final String BUCKET_NAME = "vzapbucket";

    public void uploadFile(String key, InputStream fileStream, long fileSize, String contentType) {
        S3Repo.uploadFile(BUCKET_NAME, key, fileStream, fileSize, contentType);
    }

    public InputStream downloadFile(String key) {
        return S3Repo.downloadFile(BUCKET_NAME, key);
    }
}
