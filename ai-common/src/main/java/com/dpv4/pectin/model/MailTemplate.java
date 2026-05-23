package com.dpv4.pectin.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class MailTemplate implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String subject;
    private String content;
    private String placeholders;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsed;
    private int usageCount;
    
    public MailTemplate() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.usageCount = 0;
    }
    
    public MailTemplate(String name, String subject, String content) {
        this();
        this.name = name;
        this.subject = subject;
        this.content = content;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getPlaceholders() {
        return placeholders;
    }
    
    public void setPlaceholders(String placeholders) {
        this.placeholders = placeholders;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastUsed() {
        return lastUsed;
    }
    
    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }
    
    public int getUsageCount() {
        return usageCount;
    }
    
    public void incrementUsageCount() {
        this.usageCount++;
        this.lastUsed = LocalDateTime.now();
    }
    
    public String applyPlaceholders(java.util.Map<String, String> values) {
        String result = content;
        for (java.util.Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
    
    public String applySubjectPlaceholders(java.util.Map<String, String> values) {
        String result = subject;
        for (java.util.Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}