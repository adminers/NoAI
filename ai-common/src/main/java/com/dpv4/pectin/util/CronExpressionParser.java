package com.dpv4.pectin.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CronExpressionParser {

    private static final Pattern CRON_PATTERN = Pattern.compile(
            "^\\s*(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*(\\S+)?\\s*$"
    );

    private CronExpressionParser() {}

    public static LocalDateTime parseNextExecution(String cronExpression) {
        Matcher matcher = CRON_PATTERN.matcher(cronExpression);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid cron expression: " + cronExpression);
        }

        String seconds = matcher.group(1);
        String minutes = matcher.group(2);
        String hours = matcher.group(3);
        String dayOfMonth = matcher.group(4);
        String month = matcher.group(5);
        String dayOfWeek = matcher.group(6);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextTime = now;

        int targetSecond = parseField(seconds, now.getSecond(), 0, 59);
        int targetMinute = parseField(minutes, now.getMinute(), 0, 59);
        int targetHour = parseField(hours, now.getHour(), 0, 23);
        int targetDay = parseField(dayOfMonth, now.getDayOfMonth(), 1, 31);
        int targetMonth = parseField(month, now.getMonthValue(), 1, 12);

        nextTime = nextTime.withSecond(targetSecond);
        
        if (targetMinute >= now.getMinute() && targetSecond >= now.getSecond()) {
            nextTime = nextTime.withMinute(targetMinute);
        } else {
            nextTime = nextTime.plusMinutes(1).withMinute(targetMinute);
        }

        if (targetHour >= nextTime.getHour()) {
            nextTime = nextTime.withHour(targetHour);
        } else {
            nextTime = nextTime.plusDays(1).withHour(targetHour);
        }

        if (targetDay >= nextTime.getDayOfMonth() && targetMonth == nextTime.getMonthValue()) {
            nextTime = nextTime.withDayOfMonth(targetDay);
        } else {
            nextTime = nextTime.plusMonths(1).withDayOfMonth(targetDay);
        }

        nextTime = nextTime.withMonth(targetMonth);

        if (nextTime.isBefore(now)) {
            nextTime = nextTime.plusDays(1);
        }

        return nextTime;
    }

    public static long calculateDelayMs(LocalDateTime targetTime) {
        LocalDateTime now = LocalDateTime.now();
        if (targetTime.isBefore(now)) {
            return 0;
        }
        return java.time.Duration.between(now, targetTime).toMillis();
    }

    private static int parseField(String field, int currentValue, int min, int max) {
        if ("*".equals(field)) {
            return currentValue;
        }
        if (field.contains("/")) {
            String[] parts = field.split("/");
            int base = parts[0].equals("*") ? min : Integer.parseInt(parts[0]);
            int step = Integer.parseInt(parts[1]);
            int value = base;
            while (value < currentValue) {
                value += step;
            }
            return value <= max ? value : base;
        }
        if (field.contains("-")) {
            String[] parts = field.split("-");
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);
            if (currentValue >= start && currentValue <= end) {
                return currentValue;
            }
            return start;
        }
        return Integer.parseInt(field);
    }

    public static String buildCronExpression(int minute, Integer hour, Integer day) {
        StringBuilder cron = new StringBuilder();
        cron.append("0 ");
        
        if (minute != null) {
            cron.append(minute).append(" ");
        } else {
            cron.append("* ");
        }
        
        if (hour != null) {
            cron.append(hour).append(" ");
        } else {
            cron.append("* ");
        }
        
        if (day != null) {
            cron.append(day).append(" ");
        } else {
            cron.append("* ");
        }
        
        cron.append("* *");
        return cron.toString();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}