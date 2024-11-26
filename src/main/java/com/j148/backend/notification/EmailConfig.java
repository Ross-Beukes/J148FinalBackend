package com.j148.backend.notification;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class EmailConfig {
    private static final Logger LOGGER = Logger.getLogger(EmailConfig.class.getName());
    private Properties properties;

    @PostConstruct
    public void init() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("mail.properties")) {
            if (input == null) {
                LOGGER.severe("Unable to find mail.properties");
                throw new IOException("Unable to find mail.properties");
            }
            properties.load(input);
            LOGGER.info("Email configuration loaded successfully");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error configuring email services", ex);
            throw new RuntimeException("Error configuring email services, try again later", ex);
        }
    }

    @Produces
    @ApplicationScoped
    public Properties getEmailProperties() {
        return properties;
    }

    // Getters now use the injected properties
    public String getHost() {
        return properties.getProperty("mail.smtp.host");
    }

    public String getPort() {
        return properties.getProperty("mail.smtp.port");
    }

    public String getUser() {
        return properties.getProperty("mail.smtp.user");
    }

    public String getPassword() {
        return properties.getProperty("mail.smtp.password");
    }

    public boolean isAuthEnabled() {
        return Boolean.parseBoolean(properties.getProperty("mail.smtp.auth"));
    }

    public boolean isStartTLSEnabled() {
        return Boolean.parseBoolean(properties.getProperty("mail.smtp.starttls.enable"));
    }
}