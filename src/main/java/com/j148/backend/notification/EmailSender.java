/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.notification;

/**
 *
 * @author Tshireletso
 */

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Authenticator;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
 

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EmailSender {
    
    
    public static void sendNotification(String sendTo,String notification,String subject) throws MessagingException {
        String email = "xavierdovah124";
        String password = "evba attv nsgw ymlz";
        
        //Configuring properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port","587" );
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

     
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

       
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendTo));
        message.setSubject(subject);

    
        message.setText(notification);
        Transport.send(message);
        System.out.println("Notification successfully sent");
        
    }
    
     public static void sendEmailWithAttachment(String sendTo,String body ,String subject,File attachment) throws MessagingException {
        String email = "xavierdovah124";
        String password = "evba attv nsgw ymlz";
        
        //Configuring properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port","587" );
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

     
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

       
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendTo));
        message.setSubject(subject);
        
        // Create the MimeBodyPart for the email body
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(body);

            // Create BodyPart for the attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
        try {
            attachmentPart.attachFile(attachment);
        } catch (IOException ex) {
            Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, "Error while attacing this file", ex);
        }

            // message body and attachment
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            multipart.addBodyPart(attachmentPart);

            
           
            message.setContent(multipart);
    
      
        Transport.send(message);
        System.out.println("Notification successfully sent");
        
    }
    
        
        
}
    

