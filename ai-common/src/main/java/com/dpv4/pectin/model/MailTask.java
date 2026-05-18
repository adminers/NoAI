package com.dpv4.pectin.model;

import java.time.LocalDateTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class MailTask implements Delayed {

    private final MailMessage mailMessage;
    private final LocalDateTime scheduledTime;
    private final long delayMs;
    private final int maxRetries;
    private int retryCount;

    public MailTask(MailMessage mailMessage, long delayMs, int maxRetries) {
        this.mailMessage = mailMessage;
        this.delayMs = delayMs;
        this.scheduledTime = LocalDateTime.now().plusNanos(TimeUnit.MILLISECONDS.toNanos(delayMs));
        this.maxRetries = maxRetries;
        this.retryCount = 0;
    }

    public MailMessage getMailMessage() {
        return mailMessage;
    }

    public long getDelayMs() {
        return delayMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public boolean canRetry() {
        return retryCount < maxRetries;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long remainingNanos = scheduledTime.toNanoOfDay() - LocalDateTime.now().toNanoOfDay();
        return unit.convert(remainingNanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        MailTask other = (MailTask) o;
        return Long.compare(this.scheduledTime.toNanoOfDay(), other.scheduledTime.toNanoOfDay());
    }
}