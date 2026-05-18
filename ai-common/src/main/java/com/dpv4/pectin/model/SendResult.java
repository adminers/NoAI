package com.dpv4.pectin.model;

import java.time.LocalDateTime;

public class SendResult {

    private boolean success;
    private String messageId;
    private String errorMessage;
    private LocalDateTime sendTime;
    private long durationMs;

    public SendResult() {
        this.sendTime = LocalDateTime.now();
    }

    public static SendResult success(String messageId, long durationMs) {
        SendResult result = new SendResult();
        result.success = true;
        result.messageId = messageId;
        result.durationMs = durationMs;
        return result;
    }

    public static SendResult failure(String errorMessage) {
        SendResult result = new SendResult();
        result.success = false;
        result.errorMessage = errorMessage;
        return result;
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

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }
}