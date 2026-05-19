package com.dpv4.pectin.model;

import java.time.LocalDateTime;

public class ScheduleConfig {

    private String cronExpression;
    private LocalDateTime fixedTime;
    private Long fixedDelayMs;
    private Long initialDelayMs;
    private ScheduleType scheduleType;

    public enum ScheduleType {
        CRON,
        FIXED_TIME,
        FIXED_DELAY,
        ONE_TIME
    }

    public ScheduleConfig() {}

    public static ScheduleConfig cron(String cronExpression) {
        ScheduleConfig config = new ScheduleConfig();
        config.setCronExpression(cronExpression);
        config.setScheduleType(ScheduleType.CRON);
        return config;
    }

    public static ScheduleConfig fixedTime(LocalDateTime fixedTime) {
        ScheduleConfig config = new ScheduleConfig();
        config.setFixedTime(fixedTime);
        config.setScheduleType(ScheduleType.FIXED_TIME);
        return config;
    }

    public static ScheduleConfig fixedDelay(long initialDelayMs, long fixedDelayMs) {
        ScheduleConfig config = new ScheduleConfig();
        config.setInitialDelayMs(initialDelayMs);
        config.setFixedDelayMs(fixedDelayMs);
        config.setScheduleType(ScheduleType.FIXED_DELAY);
        return config;
    }

    public static ScheduleConfig oneTime(LocalDateTime fixedTime) {
        ScheduleConfig config = new ScheduleConfig();
        config.setFixedTime(fixedTime);
        config.setScheduleType(ScheduleType.ONE_TIME);
        return config;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public LocalDateTime getFixedTime() {
        return fixedTime;
    }

    public void setFixedTime(LocalDateTime fixedTime) {
        this.fixedTime = fixedTime;
    }

    public Long getFixedDelayMs() {
        return fixedDelayMs;
    }

    public void setFixedDelayMs(Long fixedDelayMs) {
        this.fixedDelayMs = fixedDelayMs;
    }

    public Long getInitialDelayMs() {
        return initialDelayMs;
    }

    public void setInitialDelayMs(Long initialDelayMs) {
        this.initialDelayMs = initialDelayMs;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }
}