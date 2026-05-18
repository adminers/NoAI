package com.dpv4.pectin.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MailMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String to;
    private String subject;
    private String body;
    private List<String> cc;
    private List<String> bcc;
    private LocalDateTime createTime;
    private boolean html;

    public MailMessage() {
        this.cc = new ArrayList<>();
        this.bcc = new ArrayList<>();
        this.createTime = LocalDateTime.now();
        this.html = false;
    }

    public MailMessage(String to, String subject, String body) {
        this();
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }

    public void addCc(String ccAddress) {
        this.cc.add(ccAddress);
    }

    public void addBcc(String bccAddress) {
        this.bcc.add(bccAddress);
    }
}