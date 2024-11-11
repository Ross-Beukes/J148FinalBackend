package com.j148.backend.user;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    // Send email with the token
    public static void sendEmail(String recipientEmail, String token) throws MessagingException {
        String host = "smtp.gmail.com"; // or use any SMTP server
        String port = "587"; // For Gmail
        String from = "naledimodika7@gmail.com"; // preferably Use environment variable
        String password = "qtef wdud zzax qdat"; // preferably Use environment variable)

        System.out.println(from + '\n' + password);
        System.out.println(recipientEmail);
        if (from == null || password == null){
            throw  new IllegalStateException("Gmail credentials not set in environment variables.");
        }
        // Set mail properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");  // Enable STARTTLS
        properties.put("mail.smtp.ssl.trust", host); // Trust the host

        // Set up the Session
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        // Create the email message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Your Admin Registration Token");

        // Create the body of the email
        String emailContent = "Hello,\n\nHere is your registration token: \n\n" +
                "Token: " + token + "\n\n" +
                "Use this token to complete your registration.\n\n" +
                "Best regards,\nYour Application Team";

        message.setText(emailContent);

        // Send the email
        Transport.send(message);
    }
}
