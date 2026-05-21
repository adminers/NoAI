package com.dpv4.pectin.ruleengine.context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class RuleContext {
    
    private LocalDateTime executionTime;
    private Map<String, Object> variables;
    private boolean matched;
    private String matchedRuleId;
    
    public RuleContext() {
        this.executionTime = LocalDateTime.now();
        this.variables = new HashMap<>();
        this.matched = false;
    }
    
    public LocalDateTime getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }
    
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
    
    public void setVariable(String key, Object value) {
        this.variables.put(key, value);
    }
    
    public Object getVariable(String key) {
        return this.variables.get(key);
    }
    
    public boolean isMatched() {
        return matched;
    }
    
    public void setMatched(boolean matched) {
        this.matched = matched;
    }
    
    public String getMatchedRuleId() {
        return matchedRuleId;
    }
    
    public void setMatchedRuleId(String matchedRuleId) {
        this.matchedRuleId = matchedRuleId;
    }
    
    public int getCurrentHour() {
        return executionTime.getHour();
    }
    
    public int getCurrentMinute() {
        return executionTime.getMinute();
    }
    
    public int getCurrentDay() {
        return executionTime.getDayOfMonth();
    }
    
    public int getCurrentMonth() {
        return executionTime.getMonthValue();
    }
    
    public int getCurrentWeekday() {
        return executionTime.getDayOfWeek().getValue();
    }
}