package com.dpv4.pectin.repository;

import com.dpv4.pectin.model.MailTemplate;

import java.util.List;
import java.util.Optional;

public interface MailTemplateRepository {
    
    MailTemplate save(MailTemplate template);
    
    Optional<MailTemplate> findById(String id);
    
    Optional<MailTemplate> findByName(String name);
    
    List<MailTemplate> findAll();
    
    void deleteById(String id);
    
    List<MailTemplate> findRecentUsed(int limit);
}