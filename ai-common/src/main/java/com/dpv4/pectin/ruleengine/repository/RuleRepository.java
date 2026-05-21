package com.dpv4.pectin.ruleengine.repository;

import com.dpv4.pectin.ruleengine.model.Rule;

import java.util.List;
import java.util.Optional;

public interface RuleRepository {
    
    Rule save(Rule rule);
    
    Optional<Rule> findById(String id);
    
    List<Rule> findAll();
    
    List<Rule> findByEnabled(boolean enabled);
    
    void deleteById(String id);
    
    void deleteAll();
    
    long count();
}