package com.j148.backend.files.s3;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

import java.util.logging.Logger;

// S3Config.java
@ApplicationScoped
public class S3Config {
    private static final Logger LOGGER = Logger.getLogger(S3Config.class.getName());
    private static final String REGION = "af-south-1";

    private AmazonS3 s3Client;

    @PostConstruct
    public void init() {
        try {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(REGION)
                    .withCredentials(new InstanceProfileCredentialsProvider(false))
                    .build();

            LOGGER.info("S3 client initialized successfully");
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize S3 client: " + e.getMessage());
            throw new RuntimeException("Could not create S3 client", e);
        }
    }

    @Produces
    @ApplicationScoped
    public AmazonS3 s3Client() {
        return s3Client;
    }
}
