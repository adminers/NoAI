package com.dpv4.pectin.utils;

import java.io.*;
import java.util.Properties;

public final class ConfigUtils {
    
    private ConfigUtils() {}
    
    private static Properties properties = new Properties();
    private static boolean loaded = false;
    
    public static void load(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try (InputStream is = new FileInputStream(file)) {
                properties.load(is);
                loaded = true;
            } catch (IOException e) {
                System.err.println("Failed to load config file: " + e.getMessage());
            }
        }
    }
    
    public static void loadFromClasspath(String resourcePath) {
        try (InputStream is = ConfigUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                properties.load(is);
                loaded = true;
            }
        } catch (IOException e) {
            System.err.println("Failed to load config from classpath: " + e.getMessage());
        }
    }
    
    public static String getString(String key) {
        return properties.getProperty(key);
    }
    
    public static String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static int getInt(String key) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : 0;
    }
    
    public static int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
    
    public static long getLong(String key) {
        String value = properties.getProperty(key);
        return value != null ? Long.parseLong(value) : 0L;
    }
    
    public static long getLong(String key, long defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Long.parseLong(value) : defaultValue;
    }
    
    public static boolean getBoolean(String key) {
        String value = properties.getProperty(key);
        return value != null && Boolean.parseBoolean(value);
    }
    
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    public static double getDouble(String key) {
        String value = properties.getProperty(key);
        return value != null ? Double.parseDouble(value) : 0.0;
    }
    
    public static double getDouble(String key, double defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Double.parseDouble(value) : defaultValue;
    }
    
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public static boolean containsKey(String key) {
        return properties.containsKey(key);
    }
    
    public static void save(String filePath) {
        try (OutputStream os = new FileOutputStream(filePath)) {
            properties.store(os, "Configuration file");
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }
    
    public static boolean isLoaded() {
        return loaded;
    }
    
    public static void reload() {
        properties.clear();
        loaded = false;
    }
}