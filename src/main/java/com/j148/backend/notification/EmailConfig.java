/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.notification;

/**
 *
 * @author Tshireletso
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailConfig {
    
        private Properties properties;

    public EmailConfig() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("mail.properties")) {
            if (input == null) {
                throw new IOException("Sorry, unable to find mail.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error Configuring E-mail services , try again later");
        }
    }

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
