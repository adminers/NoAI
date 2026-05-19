package com.dpv4.pectin.model;

import java.time.LocalDateTime;

public class MailScheduleRecord {

    private String id;
    private String mailMessageId;
    private String to;
    private String subject;
    private LocalDateTime scheduledTime;
    private LocalDateTime actualSendTime;
    private boolean success;
    private String messageId;
    private String errorMessage;
    private long durationMs;
    private int retryCount;
    private LocalDateTime createdAt;

    public MailScheduleRecord() {
        this.createdAt = LocalDateTime.now();
        this.success = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMailMessageId() {
        return mailMessageId;
    }

    public void setMailMessageId(String mailMessageId) {
        this.mailMessageId = mailMessageId;
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

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getActualSendTime() {
        return actualSendTime;
    }

    public void setActualSendTime(LocalDateTime actualSendTime) {
        this.actualSendTime = actualSendTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}