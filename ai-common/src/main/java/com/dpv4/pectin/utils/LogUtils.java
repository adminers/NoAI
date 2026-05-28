package com.dpv4.pectin.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public final class LogUtils {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private LogUtils() {}
    
    private static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        logger.setLevel(Level.ALL);
        
        if (logger.getHandlers().length == 0) {
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
            
            SimpleFormatter formatter = new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord record) {
                    String level = String.format("%-5s", record.getLevel());
                    String time = LocalDateTime.now().format(FORMATTER);
                    String className = record.getSourceClassName();
                    if (className != null && className.contains(".")) {
                        className = className.substring(className.lastIndexOf('.') + 1);
                    }
                    String methodName = record.getSourceMethodName();
                    String message = formatMessage(record);
                    
                    return String.format("[%s] [%s] [%s.%s] %s%n", 
                            time, level, className, methodName, message);
                }
            };
            
            handler.setFormatter(formatter);
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
        }
        
        return logger;
    }
    
    public static void trace(Class<?> clazz, String message) {
        getLogger(clazz).log(Level.FINEST, message);
    }
    
    public static void trace(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).log(Level.FINEST, message, throwable);
    }
    
    public static void debug(Class<?> clazz, String message) {
        getLogger(clazz).log(Level.FINE, message);
    }
    
    public static void debug(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).log(Level.FINE, message, throwable);
    }
    
    public static void info(Class<?> clazz, String message) {
        getLogger(clazz).log(Level.INFO, message);
    }
    
    public static void info(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).log(Level.INFO, message, throwable);
    }
    
    public static void warn(Class<?> clazz, String message) {
        getLogger(clazz).log(Level.WARNING, message);
    }
    
    public static void warn(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).log(Level.WARNING, message, throwable);
    }
    
    public static void error(Class<?> clazz, String message) {
        getLogger(clazz).log(Level.SEVERE, message);
    }
    
    public static void error(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).log(Level.SEVERE, message, throwable);
    }
    
    public static void log(Class<?> clazz, Level level, String message) {
        getLogger(clazz).log(level, message);
    }
    
    public static void log(Class<?> clazz, Level level, String message, Throwable throwable) {
        getLogger(clazz).log(level, message, throwable);
    }
    
    public static void setLevel(Class<?> clazz, Level level) {
        getLogger(clazz).setLevel(level);
    }
    
    public static void setGlobalLevel(Level level) {
        Logger.getLogger("").setLevel(level);
    }
    
    public static void printStackTrace(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
    
    public static String getStackTraceString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}