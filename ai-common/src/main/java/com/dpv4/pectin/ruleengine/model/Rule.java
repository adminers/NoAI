package com.dpv4.pectin.ruleengine.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Rule implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String description;
    private List<Condition> conditions = new ArrayList<>();
    private List<Action> actions = new ArrayList<>();
    private boolean enabled;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private LocalDateTime lastExecuted;
    private int executionCount;
    private int successCount;
    
    public Rule() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.enabled = true;
        this.active = false;
        this.executionCount = 0;
        this.successCount = 0;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<Condition> getConditions() {
        return conditions;
    }
    
    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
    
    public void addCondition(Condition condition) {
        condition.setId(UUID.randomUUID().toString());
        this.conditions.add(condition);
    }
    
    public void removeCondition(String conditionId) {
        this.conditions.removeIf(c -> c.getId().equals(conditionId));
    }
    
    public List<Action> getActions() {
        return actions;
    }
    
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
    
    public void addAction(Action action) {
        action.setId(UUID.randomUUID().toString());
        this.actions.add(action);
    }
    
    public void removeAction(String actionId) {
        this.actions.removeIf(a -> a.getId().equals(actionId));
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
    
    public LocalDateTime getLastExecuted() {
        return lastExecuted;
    }
    
    public void setLastExecuted(LocalDateTime lastExecuted) {
        this.lastExecuted = lastExecuted;
    }
    
    public int getExecutionCount() {
        return executionCount;
    }
    
    public void incrementExecutionCount() {
        this.executionCount++;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public void incrementSuccessCount() {
        this.successCount++;
    }
    
    public void updateModifiedTime() {
        this.lastModified = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return name + " (" + (enabled ? "Enabled" : "Disabled") + ")";
    }
}