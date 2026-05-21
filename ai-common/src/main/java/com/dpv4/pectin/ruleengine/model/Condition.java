package com.dpv4.pectin.ruleengine.model;

import java.io.Serializable;

public class Condition implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private ConditionType type;
    private String field;
    private Operator operator;
    private String value;
    private String description;
    
    public enum ConditionType {
        TIME,
        DATE,
        WEEKDAY,
        RECIPIENT,
        SUBJECT,
        CUSTOM
    }
    
    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        CONTAINS,
        NOT_CONTAINS,
        GREATER_THAN,
        LESS_THAN,
        BETWEEN,
        IN
    }
    
    public Condition() {}
    
    public Condition(ConditionType type, String field, Operator operator, String value) {
        this.type = type;
        this.field = field;
        this.operator = operator;
        this.value = value;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public ConditionType getType() {
        return type;
    }
    
    public void setType(ConditionType type) {
        this.type = type;
    }
    
    public String getField() {
        return field;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public Operator getOperator() {
        return operator;
    }
    
    public void setOperator(Operator operator) {
        this.operator = operator;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return String.format("%s %s %s", field, operator, value);
    }
}