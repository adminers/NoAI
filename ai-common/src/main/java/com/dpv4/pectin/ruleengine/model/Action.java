package com.dpv4.pectin.ruleengine.model;

import java.io.Serializable;

public class Action implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private ActionType type;
    private String parameter;
    private String description;
    
    public enum ActionType {
        SEND_EMAIL,
        SEND_EMAIL_WITH_ATTACHMENT,
        DELAY_SEND,
        CANCEL_PENDING,
        LOG_MESSAGE,
        EXECUTE_SCRIPT
    }
    
    public Action() {}
    
    public Action(ActionType type, String parameter) {
        this.type = type;
        this.parameter = parameter;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public ActionType getType() {
        return type;
    }
    
    public void setType(ActionType type) {
        this.type = type;
    }
    
    public String getParameter() {
        return parameter;
    }
    
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return type + (parameter != null ? " (" + parameter + ")" : "");
    }
}