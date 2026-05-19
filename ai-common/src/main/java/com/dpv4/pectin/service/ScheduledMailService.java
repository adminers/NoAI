package com.dpv4.pectin.service;

import com.dpv4.pectin.model.*;
import com.dpv4.pectin.repository.MailRecordRepository;
import com.dpv4.pectin.util.CronExpressionParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class ScheduledMailService {

    @Autowired
    private MailService mailService;

    @Autowired
    private MailRecordRepository recordRepository;

    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public String scheduleMail(MailMessage mailMessage, ScheduleConfig config) {
        String taskId = UUID.randomUUID().toString();
        MailScheduleRecord record = createRecord(mailMessage, taskId);

        switch (config.getScheduleType()) {
            case CRON:
                scheduleCronTask(mailMessage, config.getCronExpression(), taskId, record);
                break;
            case FIXED_TIME:
                scheduleFixedTimeTask(mailMessage, config.getFixedTime(), taskId, record);
                break;
            case FIXED_DELAY:
                scheduleFixedDelayTask(mailMessage, config.getInitialDelayMs(), config.getFixedDelayMs(), taskId, record);
                break;
            case ONE_TIME:
                scheduleOneTimeTask(mailMessage, config.getFixedTime(), taskId, record);
                break;
        }

        return taskId;
    }

    public void scheduleMailByMinute(MailMessage mailMessage, int minuteOfHour) {
        String cron = String.format("0 %d * * * *", minuteOfHour);
        ScheduleConfig config = ScheduleConfig.cron(cron);
        scheduleMail(mailMessage, config);
    }

    public void scheduleMailByHour(MailMessage mailMessage, int minuteOfHour, int hourOfDay) {
        String cron = String.format("0 %d %d * * *", minuteOfHour, hourOfDay);
        ScheduleConfig config = ScheduleConfig.cron(cron);
        scheduleMail(mailMessage, config);
    }

    public void scheduleMailByDay(MailMessage mailMessage, int minuteOfHour, int hourOfDay, int dayOfMonth) {
        String cron = String.format("0 %d %d %d * *", minuteOfHour, hourOfDay, dayOfMonth);
        ScheduleConfig config = ScheduleConfig.cron(cron);
        scheduleMail(mailMessage, config);
    }

    public void scheduleMailAtTime(MailMessage mailMessage, LocalDateTime scheduledTime) {
        ScheduleConfig config = ScheduleConfig.oneTime(scheduledTime);
        scheduleMail(mailMessage, config);
    }

    public void scheduleRecurringMail(MailMessage mailMessage, long initialDelayMs, long periodMs) {
        ScheduleConfig config = ScheduleConfig.fixedDelay(initialDelayMs, periodMs);
        scheduleMail(mailMessage, config);
    }

    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null && !future.isCancelled()) {
            future.cancel(false);
        }
    }

    private void scheduleCronTask(MailMessage mailMessage, String cron, String taskId, MailScheduleRecord record) {
        LocalDateTime nextTime = CronExpressionParser.parseNextExecution(cron);
        long delayMs = CronExpressionParser.calculateDelayMs(nextTime);

        ScheduledFuture<?> future = java.util.concurrent.Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> {
                    executeAndRecord(mailMessage, record);
                }, delayMs, 24 * 60 * 60 * 1000, java.util.concurrent.TimeUnit.MILLISECONDS);

        scheduledTasks.put(taskId, future);
        record.setScheduledTime(nextTime);
        recordRepository.save(record);
    }

    private void scheduleFixedTimeTask(MailMessage mailMessage, LocalDateTime fixedTime, String taskId, MailScheduleRecord record) {
        LocalDateTime now = LocalDateTime.now();
        
        if (fixedTime.isBefore(now)) {
            fixedTime = fixedTime.plusDays(1);
        }

        long delayMs = CronExpressionParser.calculateDelayMs(fixedTime);

        ScheduledFuture<?> future = java.util.concurrent.Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> {
                    executeAndRecord(mailMessage, record);
                }, delayMs, 24 * 60 * 60 * 1000, java.util.concurrent.TimeUnit.MILLISECONDS);

        scheduledTasks.put(taskId, future);
        record.setScheduledTime(fixedTime);
        recordRepository.save(record);
    }

    private void scheduleFixedDelayTask(MailMessage mailMessage, Long initialDelayMs, Long periodMs, String taskId, MailScheduleRecord record) {
        if (initialDelayMs == null) initialDelayMs = 0L;
        if (periodMs == null) periodMs = 60000L;

        ScheduledFuture<?> future = java.util.concurrent.Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> {
                    executeAndRecord(mailMessage, record);
                }, initialDelayMs, periodMs, java.util.concurrent.TimeUnit.MILLISECONDS);

        scheduledTasks.put(taskId, future);
        record.setScheduledTime(LocalDateTime.now().plusNanos(
                java.util.concurrent.TimeUnit.MILLISECONDS.toNanos(initialDelayMs)));
        recordRepository.save(record);
    }

    private void scheduleOneTimeTask(MailMessage mailMessage, LocalDateTime fixedTime, String taskId, MailScheduleRecord record) {
        long delayMs = CronExpressionParser.calculateDelayMs(fixedTime);

        ScheduledFuture<?> future = java.util.concurrent.Executors.newScheduledThreadPool(1)
                .schedule(() -> {
                    executeAndRecord(mailMessage, record);
                    scheduledTasks.remove(taskId);
                }, delayMs, java.util.concurrent.TimeUnit.MILLISECONDS);

        scheduledTasks.put(taskId, future);
        record.setScheduledTime(fixedTime);
        recordRepository.save(record);
    }

    private void executeAndRecord(MailMessage mailMessage, MailScheduleRecord record) {
        SendResult result = mailService.sendWithRetry(mailMessage, 3);
        
        record.setActualSendTime(LocalDateTime.now());
        record.setSuccess(result.isSuccess());
        record.setMessageId(result.getMessageId());
        record.setErrorMessage(result.getErrorMessage());
        record.setDurationMs(result.getDurationMs());
        
        recordRepository.save(record);

        System.out.println("Mail task executed: " + record.getId() + ", success: " + result.isSuccess());
    }

    private MailScheduleRecord createRecord(MailMessage mailMessage, String taskId) {
        MailScheduleRecord record = new MailScheduleRecord();
        record.setId(taskId);
        record.setMailMessageId(UUID.randomUUID().toString());
        record.setTo(mailMessage.getTo());
        record.setSubject(mailMessage.getSubject());
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }
}