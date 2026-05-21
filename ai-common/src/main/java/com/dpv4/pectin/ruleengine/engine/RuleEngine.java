package com.dpv4.pectin.ruleengine.engine;

import com.dpv4.pectin.ruleengine.context.RuleContext;
import com.dpv4.pectin.ruleengine.model.Action;
import com.dpv4.pectin.ruleengine.model.Condition;
import com.dpv4.pectin.ruleengine.model.Rule;
import com.dpv4.pectin.ruleengine.repository.RuleRepository;
import com.dpv4.pectin.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class RuleEngine {
    
    @Autowired
    private RuleRepository ruleRepository;
    
    @Autowired
    private MailService mailService;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    
    public RuleContext evaluate() {
        RuleContext context = new RuleContext();
        
        List<Rule> enabledRules = ruleRepository.findByEnabled(true);
        
        for (Rule rule : enabledRules) {
            if (evaluateConditions(rule, context)) {
                context.setMatched(true);
                context.setMatchedRuleId(rule.getId());
                executeActions(rule, context);
                updateRuleStats(rule);
            }
        }
        
        return context;
    }
    
    public RuleContext evaluateRule(Rule rule) {
        RuleContext context = new RuleContext();
        
        if (rule.isEnabled() && evaluateConditions(rule, context)) {
            context.setMatched(true);
            context.setMatchedRuleId(rule.getId());
            executeActions(rule, context);
            updateRuleStats(rule);
        }
        
        return context;
    }
    
    private boolean evaluateConditions(Rule rule, RuleContext context) {
        List<Condition> conditions = rule.getConditions();
        
        if (conditions.isEmpty()) {
            return true;
        }
        
        for (Condition condition : conditions) {
            if (!evaluateCondition(condition, context)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean evaluateCondition(Condition condition, RuleContext context) {
        switch (condition.getType()) {
            case TIME:
                return evaluateTimeCondition(condition, context);
            case DATE:
                return evaluateDateCondition(condition, context);
            case WEEKDAY:
                return evaluateWeekdayCondition(condition, context);
            case RECIPIENT:
            case SUBJECT:
            case CUSTOM:
            default:
                return evaluateCustomCondition(condition, context);
        }
    }
    
    private boolean evaluateTimeCondition(Condition condition, RuleContext context) {
        String targetTime = condition.getValue();
        String[] parts = targetTime.split(":");
        
        if (parts.length == 2) {
            int targetHour = Integer.parseInt(parts[0]);
            int targetMinute = Integer.parseInt(parts[1]);
            
            return context.getCurrentHour() == targetHour && 
                   context.getCurrentMinute() == targetMinute;
        }
        
        return false;
    }
    
    private boolean evaluateDateCondition(Condition condition, RuleContext context) {
        String targetDate = condition.getValue();
        String[] parts = targetDate.split("-");
        
        if (parts.length == 3) {
            int targetDay = Integer.parseInt(parts[2]);
            
            return context.getCurrentDay() == targetDay;
        }
        
        return false;
    }
    
    private boolean evaluateWeekdayCondition(Condition condition, RuleContext context) {
        int targetWeekday = Integer.parseInt(condition.getValue());
        return context.getCurrentWeekday() == targetWeekday;
    }
    
    private boolean evaluateCustomCondition(Condition condition, RuleContext context) {
        Object fieldValue = context.getVariable(condition.getField());
        String targetValue = condition.getValue();
        
        if (fieldValue == null) {
            return false;
        }
        
        String fieldStrValue = fieldValue.toString();
        
        switch (condition.getOperator()) {
            case EQUALS:
                return fieldStrValue.equals(targetValue);
            case NOT_EQUALS:
                return !fieldStrValue.equals(targetValue);
            case CONTAINS:
                return fieldStrValue.contains(targetValue);
            case NOT_CONTAINS:
                return !fieldStrValue.contains(targetValue);
            default:
                return false;
        }
    }
    
    private void executeActions(Rule rule, RuleContext context) {
        for (Action action : rule.getActions()) {
            executeAction(action, context);
        }
    }
    
    private void executeAction(Action action, RuleContext context) {
        switch (action.getType()) {
            case SEND_EMAIL:
                executeSendEmail(action, context);
                break;
            case SEND_EMAIL_WITH_ATTACHMENT:
                executeSendEmailWithAttachment(action, context);
                break;
            case DELAY_SEND:
                executeDelaySend(action, context);
                break;
            case CANCEL_PENDING:
                executeCancelPending(action, context);
                break;
            case LOG_MESSAGE:
                executeLogMessage(action, context);
                break;
            case EXECUTE_SCRIPT:
                executeScript(action, context);
                break;
        }
    }
    
    private void executeSendEmail(Action action, RuleContext context) {
        System.out.println("Executing SEND_EMAIL action: " + action.getParameter());
    }
    
    private void executeSendEmailWithAttachment(Action action, RuleContext context) {
        System.out.println("Executing SEND_EMAIL_WITH_ATTACHMENT action: " + action.getParameter());
    }
    
    private void executeDelaySend(Action action, RuleContext context) {
        System.out.println("Executing DELAY_SEND action: " + action.getParameter());
    }
    
    private void executeCancelPending(Action action, RuleContext context) {
        System.out.println("Executing CANCEL_PENDING action: " + action.getParameter());
    }
    
    private void executeLogMessage(Action action, RuleContext context) {
        System.out.println("Rule Engine Log: " + action.getParameter());
    }
    
    private void executeScript(Action action, RuleContext context) {
        System.out.println("Executing script: " + action.getParameter());
    }
    
    private void updateRuleStats(Rule rule) {
        rule.incrementExecutionCount();
        rule.incrementSuccessCount();
        rule.setLastExecuted(LocalDateTime.now());
        ruleRepository.save(rule);
    }
    
    public void evaluateAsync() {
        executorService.submit(this::evaluate);
    }
    
    public void shutdown() {
        executorService.shutdown();
    }
}