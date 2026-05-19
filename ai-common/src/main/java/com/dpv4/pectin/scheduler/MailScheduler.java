package com.dpv4.pectin.scheduler;

import com.dpv4.pectin.config.MailConfig;
import com.dpv4.pectin.model.MailMessage;
import com.dpv4.pectin.model.ScheduleConfig;
import com.dpv4.pectin.service.ScheduledMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MailScheduler {

    @Autowired
    private ScheduledMailService scheduledMailService;

    @Autowired
    private MailConfig mailConfig;

    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void init() {
        scheduler = Executors.newScheduledThreadPool(2);
        if (mailConfig.isMailEnabled()) {
            start();
        }
    }

    public void start() {
        System.out.println("MailScheduler started");
    }

    public String scheduleMail(MailMessage mailMessage, ScheduleConfig config) {
        return scheduledMailService.scheduleMail(mailMessage, config);
    }

    public String scheduleMailByMinute(MailMessage mailMessage, int minuteOfHour) {
        scheduledMailService.scheduleMailByMinute(mailMessage, minuteOfHour);
        return "scheduled_by_minute_" + minuteOfHour;
    }

    public String scheduleMailByHour(MailMessage mailMessage, int minuteOfHour, int hourOfDay) {
        scheduledMailService.scheduleMailByHour(mailMessage, minuteOfHour, hourOfDay);
        return "scheduled_by_hour_" + hourOfDay + "_" + minuteOfHour;
    }

    public String scheduleMailByDay(MailMessage mailMessage, int minuteOfHour, int hourOfDay, int dayOfMonth) {
        scheduledMailService.scheduleMailByDay(mailMessage, minuteOfHour, hourOfDay, dayOfMonth);
        return "scheduled_by_day_" + dayOfMonth + "_" + hourOfDay + "_" + minuteOfHour;
    }

    public String scheduleMailAtTime(MailMessage mailMessage, LocalDateTime scheduledTime) {
        return scheduledMailService.scheduleMailAtTime(mailMessage, scheduledTime);
    }

    public String scheduleRecurringMail(MailMessage mailMessage, long initialDelayMs, long periodMs) {
        scheduledMailService.scheduleRecurringMail(mailMessage, initialDelayMs, periodMs);
        return "recurring_task";
    }

    public void cancelTask(String taskId) {
        scheduledMailService.cancelTask(taskId);
    }

    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("MailScheduler shutdown");
    }
}