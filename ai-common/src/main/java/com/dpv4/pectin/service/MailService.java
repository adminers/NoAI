package com.dpv4.pectin.service;

import com.dpv4.pectin.config.MailConfig;
import com.dpv4.pectin.model.MailMessage;
import com.dpv4.pectin.model.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class MailService {

    @Autowired
    private MailConfig mailConfig;

    public SendResult send(MailMessage mailMessage) {
        if (!mailConfig.isMailEnabled()) {
            return SendResult.failure("Mail service is disabled");
        }

        long startMs = System.currentTimeMillis();

        try {
            Session session = createSession();
            Message message = createMessage(session, mailMessage);

            Transport transport = session.getTransport();
            transport.connect(
                    mailConfig.getSmtpHost(),
                    mailConfig.getSmtpPort(),
                    mailConfig.getUsername(),
                    mailConfig.getPassword()
            );

            Transport.send(message, message.getAllRecipients());
            transport.close();

            long durationMs = System.currentTimeMillis() - startMs;
            String messageId = message.getMessageID();

            System.out.println("Mail sent successfully to: " + mailMessage.getTo());
            return SendResult.success(messageId, durationMs);

        } catch (AuthenticationFailedException e) {
            System.err.println("Mail authentication failed: " + e.getMessage());
            return SendResult.failure("Authentication failed: " + e.getMessage());
        } catch (MessagingException e) {
            System.err.println("Mail send failed: " + e.getMessage());
            return SendResult.failure("Send failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error sending mail: " + e.getMessage());
            return SendResult.failure("Unexpected error: " + e.getMessage());
        }
    }

    public SendResult sendWithRetry(MailMessage mailMessage, int maxRetries) {
        SendResult lastResult = null;

        for (int i = 0; i <= maxRetries; i++) {
            lastResult = send(mailMessage);

            if (lastResult.isSuccess()) {
                return lastResult;
            }

            if (i < maxRetries) {
                try {
                    Thread.sleep(1000 * (i + 1));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        return lastResult != null ? lastResult : SendResult.failure("All retries exhausted");
    }

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", mailConfig.isAuth());
        props.put("mail.smtp.starttls.enable", mailConfig.isStarttls());
        props.put("mail.smtp.host", mailConfig.getSmtpHost());
        props.put("mail.smtp.port", mailConfig.getSmtpPort());

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        mailConfig.getUsername(),
                        mailConfig.getPassword()
                );
            }
        });
    }

    private Message createMessage(Session session, MailMessage mailMessage) throws MessagingException {
        MimeMessage message = new MimeMessage(session);

        String fromAddress = mailConfig.getFromAddress();
        if (fromAddress == null || fromAddress.isEmpty()) {
            fromAddress = mailConfig.getUsername();
        }
        message.setFrom(new InternetAddress(fromAddress));

        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailMessage.getTo()));

        if (mailMessage.getCc() != null && !mailMessage.getCc().isEmpty()) {
            Address[] ccAddresses = new InternetAddress[mailMessage.getCc().size()];
            for (int i = 0; i < mailMessage.getCc().size(); i++) {
                ccAddresses[i] = new InternetAddress(mailMessage.getCc().get(i));
            }
            message.setRecipients(Message.RecipientType.CC, ccAddresses);
        }

        if (mailMessage.getBcc() != null && !mailMessage.getBcc().isEmpty()) {
            Address[] bccAddresses = new InternetAddress[mailMessage.getBcc().size()];
            for (int i = 0; i < mailMessage.getBcc().size(); i++) {
                bccAddresses[i] = new InternetAddress(mailMessage.getBcc().get(i));
            }
            message.setRecipients(Message.RecipientType.BCC, bccAddresses);
        }

        message.setSubject(mailMessage.getSubject());

        if (mailMessage.isHtml()) {
            message.setContent(mailMessage.getBody(), "text/html; charset=utf-8");
        } else {
            message.setText(mailMessage.getBody());
        }

        return message;
    }
}