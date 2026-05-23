package com.dpv4.pectin.repository;

import com.dpv4.pectin.model.MailTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class FileMailTemplateRepository implements MailTemplateRepository {
    
    private static final String STORAGE_FILE = "mail_templates.json";
    private final ConcurrentHashMap<String, MailTemplate> templates = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    
    public FileMailTemplateRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        loadTemplates();
    }
    
    @Override
    public MailTemplate save(MailTemplate template) {
        templates.put(template.getId(), template);
        saveTemplates();
        return template;
    }
    
    @Override
    public Optional<MailTemplate> findById(String id) {
        return Optional.ofNullable(templates.get(id));
    }
    
    @Override
    public Optional<MailTemplate> findByName(String name) {
        return templates.values().stream()
                .filter(t -> t.getName().equals(name))
                .findFirst();
    }
    
    @Override
    public List<MailTemplate> findAll() {
        return new ArrayList<>(templates.values());
    }
    
    @Override
    public void deleteById(String id) {
        templates.remove(id);
        saveTemplates();
    }
    
    @Override
    public List<MailTemplate> findRecentUsed(int limit) {
        return templates.values().stream()
                .sorted(Comparator.comparing(MailTemplate::getLastUsed, 
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    private void loadTemplates() {
        File file = new File(STORAGE_FILE);
        if (file.exists()) {
            try {
                MailTemplate[] loadedTemplates = objectMapper.readValue(file, MailTemplate[].class);
                for (MailTemplate template : loadedTemplates) {
                    templates.put(template.getId(), template);
                }
            } catch (IOException e) {
                System.err.println("Failed to load templates: " + e.getMessage());
            }
        }
    }
    
    private void saveTemplates() {
        try {
            List<MailTemplate> templateList = new ArrayList<>(templates.values());
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(STORAGE_FILE), templateList);
        } catch (IOException e) {
            System.err.println("Failed to save templates: " + e.getMessage());
        }
    }
}