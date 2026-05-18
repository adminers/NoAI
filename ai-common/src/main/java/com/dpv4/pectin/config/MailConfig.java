package com.dpv4.pectin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    @Value("${mail.smtp.host:smtp.gmail.com}")
    private String smtpHost;

    @Value("${mail.smtp.port:587}")
    private int smtpPort;

    @Value("${mail.smtp.username:}")
    private String username;

    @Value("${mail.smtp.password:}")
    private String password;

    @Value("${mail.smtp.auth:true}")
    private boolean auth;

    @Value("${mail.smtp.starttls:true}")
    private boolean starttls;

    @Value("${mail.from:}")
    private String fromAddress;

    @Value("${mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${mail.send.interval:60000}")
    private long sendInterval;

    public String getSmtpHost() {
        return smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAuth() {
        return auth;
    }

    public boolean isStarttls() {
        return starttls;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public boolean isMailEnabled() {
        return mailEnabled;
    }

    public long getSendInterval() {
        return sendInterval;
    }
}