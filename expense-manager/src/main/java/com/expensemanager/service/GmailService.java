package com.expensemanager.service;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;

import java.util.Properties;
public class GmailService {

    private final String from;
    private final String appPassword;

    public GmailService(String from, String appPassword) {
        this.from        = from;
        this.appPassword = appPassword;
    }

    /**
     * Send email with optional attachment.
     *
     * @param to          recipient email
     * @param subject     email subject
     * @param bodyHtml    HTML body
     * @param attachment  byte[] attachment (null = no attachment)
     * @param attachName  filename for attachment (e.g. "report.pdf")
     * @param mimeType    MIME type (e.g. "application/pdf")
     */
    public void sendMail(String to, String subject, String bodyHtml,
                         byte[] attachment, String attachName, String mimeType)
            throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.host",            "smtp.gmail.com");
        props.put("mail.smtp.port",            "587");
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust",       "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, appPassword);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject, "UTF-8");

        MimeMultipart multipart = new MimeMultipart();

        // HTML body part
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(bodyHtml, "text/html; charset=UTF-8");
        multipart.addBodyPart(bodyPart);

        // Attachment part
        if (attachment != null && attachName != null) {
            MimeBodyPart attachPart = new MimeBodyPart();
            DataSource ds = new ByteArrayDataSource(attachment, mimeType);
            attachPart.setDataHandler(new DataHandler(ds));
            attachPart.setFileName(attachName);
            multipart.addBodyPart(attachPart);
        }

        msg.setContent(multipart);
        Transport.send(msg);
    }
}
