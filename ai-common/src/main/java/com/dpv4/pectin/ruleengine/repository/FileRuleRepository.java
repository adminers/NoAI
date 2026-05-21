package com.dpv4.pectin.ruleengine.repository;

import com.dpv4.pectin.ruleengine.model.Rule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class FileRuleRepository implements RuleRepository {
    
    private static final String STORAGE_FILE = "rules.json";
    private final ConcurrentHashMap<String, Rule> rules = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    
    public FileRuleRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        loadRules();
    }
    
    @Override
    public Rule save(Rule rule) {
        rule.updateModifiedTime();
        rules.put(rule.getId(), rule);
        saveRules();
        return rule;
    }
    
    @Override
    public Optional<Rule> findById(String id) {
        return Optional.ofNullable(rules.get(id));
    }
    
    @Override
    public List<Rule> findAll() {
        return new ArrayList<>(rules.values());
    }
    
    @Override
    public List<Rule> findByEnabled(boolean enabled) {
        return rules.values().stream()
                .filter(rule -> rule.isEnabled() == enabled)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(String id) {
        rules.remove(id);
        saveRules();
    }
    
    @Override
    public void deleteAll() {
        rules.clear();
        saveRules();
    }
    
    @Override
    public long count() {
        return rules.size();
    }
    
    private void loadRules() {
        File file = new File(STORAGE_FILE);
        if (file.exists()) {
            try {
                Rule[] loadedRules = objectMapper.readValue(file, Rule[].class);
                for (Rule rule : loadedRules) {
                    rules.put(rule.getId(), rule);
                }
            } catch (IOException e) {
                System.err.println("Failed to load rules from file: " + e.getMessage());
            }
        }
    }
    
    private void saveRules() {
        try {
            List<Rule> ruleList = new ArrayList<>(rules.values());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(STORAGE_FILE), ruleList);
        } catch (IOException e) {
            System.err.println("Failed to save rules to file: " + e.getMessage());
        }
    }
}