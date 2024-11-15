package com.j148.backend.user;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService {

    // Send email with the token
    public static void sendEmail(String recipientEmail, String subject, String messageBody) throws MessagingException {
        String host = "smtp.gmail.com"; // or use any SMTP server
        String port = "587"; // For Gmail
        String from = "naledimodika7@gmail.com"; // preferably Use environment variable
        String password = "qtef wdud zzax qdat"; // preferably Use environment variable)

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
        message.setSubject(subject);

        message.setText(messageBody);

        // Send the email
        Transport.send(message);
    }

    public static boolean sendVerificationEmail(String recipientEmail, String verificationLink) {
        final String senderEmail = "naledimodika7@gmail.com"; // Your Gmail email
        final String password = "qtef wdud zzax qdat"; // Your Gmail app password (not your regular password)

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Email Verification");
            message.setText("Click the following link to verify your email: " + verificationLink);

            Transport.send(message);
            System.out.println("Verification email sent successfully");
            return true;
        } catch (MessagingException e) {
            Logger.getLogger(EmailService.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }
}
