package com.dpv4.pectin.utils;

import java.util.Collection;
import java.util.Map;

public final class ValidationUtils {
    
    private ValidationUtils() {}
    
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notNull(Object obj) {
        notNull(obj, "参数不能为空");
    }
    
    public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(String str) {
        notEmpty(str, "字符串不能为空");
    }
    
    public static void notBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notBlank(String str) {
        notBlank(str, "字符串不能为空或空白");
    }
    
    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(Collection<?> collection) {
        notEmpty(collection, "集合不能为空");
    }
    
    public static void notEmpty(Map<?, ?> map, String message) {
        if (CollectionUtils.isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(Map<?, ?> map) {
        notEmpty(map, "Map不能为空");
    }
    
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isTrue(boolean condition) {
        isTrue(condition, "条件不满足");
    }
    
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isFalse(boolean condition) {
        isFalse(condition, "条件不满足");
    }
    
    public static void isEmail(String email, String message) {
        if (!StringUtils.isEmail(email)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isEmail(String email) {
        isEmail(email, "不是有效的邮箱地址");
    }
    
    public static void isPhone(String phone, String message) {
        if (!StringUtils.isPhone(phone)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isPhone(String phone) {
        isPhone(phone, "不是有效的手机号码");
    }
    
    public static void isPositive(int number, String message) {
        if (number <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isPositive(int number) {
        isPositive(number, "数字必须大于0");
    }
    
    public static void isPositive(long number, String message) {
        if (number <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isPositive(long number) {
        isPositive(number, "数字必须大于0");
    }
    
    public static void isNonNegative(int number, String message) {
        if (number < 0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isNonNegative(int number) {
        isNonNegative(number, "数字不能为负数");
    }
    
    public static void isInRange(int number, int min, int max, String message) {
        if (number < min || number > max) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isInRange(int number, int min, int max) {
        isInRange(number, min, max, "数字超出范围");
    }
    
    public static void isInRange(long number, long min, long max, String message) {
        if (number < min || number > max) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isInRange(long number, long min, long max) {
        isInRange(number, min, max, "数字超出范围");
    }
    
    public static void isSizeBetween(String str, int min, int max, String message) {
        if (str == null || str.length() < min || str.length() > max) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isSizeBetween(String str, int min, int max) {
        isSizeBetween(str, min, max, "字符串长度超出范围");
    }
    
    public static void isSizeBetween(Collection<?> collection, int min, int max, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            if (min > 0) {
                throw new IllegalArgumentException(message);
            }
        } else {
            int size = collection.size();
            if (size < min || size > max) {
                throw new IllegalArgumentException(message);
            }
        }
    }
    
    public static void isSizeBetween(Collection<?> collection, int min, int max) {
        isSizeBetween(collection, min, max, "集合大小超出范围");
    }
    
    public static boolean isValidEmail(String email) {
        return StringUtils.isEmail(email);
    }
    
    public static boolean isValidPhone(String phone) {
        return StringUtils.isPhone(phone);
    }
    
    public static boolean isValidUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        try {
            new java.net.URL(url);
            return true;
        } catch (java.net.MalformedURLException e) {
            return false;
        }
    }
    
    public static boolean isValidIpAddress(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
    
    public static void validateEmail(String email) {
        validateEmail(email, "无效的邮箱地址");
    }
    
    public static void validateEmail(String email, String message) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void validatePhone(String phone) {
        validatePhone(phone, "无效的手机号码");
    }
    
    public static void validatePhone(String phone, String message) {
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void validateUrl(String url) {
        validateUrl(url, "无效的URL");
    }
    
    public static void validateUrl(String url, String message) {
        if (!isValidUrl(url)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void validateIpAddress(String ip) {
        validateIpAddress(ip, "无效的IP地址");
    }
    
    public static void validateIpAddress(String ip, String message) {
        if (!isValidIpAddress(ip)) {
            throw new IllegalArgumentException(message);
        }
    }
}