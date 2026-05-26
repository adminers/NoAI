package com.dpv4.pectin.service;

import com.dpv4.pectin.model.MailMessage;
import com.dpv4.pectin.model.MailTemplate;
import com.dpv4.pectin.repository.MailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TemplateService {

    @Autowired
    private MailTemplateRepository templateRepository;

    public MailTemplate createTemplate(String name, String subject, String content) {
        MailTemplate template = new MailTemplate(name, subject, content);
        return templateRepository.save(template);
    }

    public MailTemplate updateTemplate(String id, String name, String subject, String content) {
        return templateRepository.findById(id)
                .map(template -> {
                    template.setName(name);
                    template.setSubject(subject);
                    template.setContent(content);
                    return templateRepository.save(template);
                })
                .orElse(null);
    }

    public Optional<MailTemplate> findById(String id) {
        return templateRepository.findById(id);
    }

    public Optional<MailTemplate> findByName(String name) {
        return templateRepository.findByName(name);
    }

    public List<MailTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    public void deleteTemplate(String id) {
        templateRepository.deleteById(id);
    }

    public MailMessage createMailFromTemplate(String templateName, String to, Map<String, String> placeholders) {
        return templateRepository.findByName(templateName)
                .map(template -> {
                    template.incrementUsageCount();
                    templateRepository.save(template);

                    String subject = template.applySubjectPlaceholders(placeholders);
                    String content = template.applyPlaceholders(placeholders);

                    MailMessage mail = new MailMessage();
                    mail.setTo(to);
                    mail.setSubject(subject);
                    mail.setBody(content);
                    return mail;
                })
                .orElse(null);
    }

    public MailMessage createMailFromTemplate(String templateId, String to, String cc, 
                                               Map<String, String> placeholders) {
        return templateRepository.findById(templateId)
                .map(template -> {
                    template.incrementUsageCount();
                    templateRepository.save(template);

                    String subject = template.applySubjectPlaceholders(placeholders);
                    String content = template.applyPlaceholders(placeholders);

                    MailMessage mail = new MailMessage();
                    mail.setTo(to);
                    if (cc != null && !cc.isEmpty()) {
                        mail.addCc(cc);
                    }
                    mail.setSubject(subject);
                    mail.setBody(content);
                    return mail;
                })
                .orElse(null);
    }

    public List<MailTemplate> getRecentUsedTemplates(int limit) {
        return templateRepository.findRecentUsed(limit);
    }

    public int getTemplateCount() {
        return templateRepository.findAll().size();
    }

    public boolean templateExists(String name) {
        return templateRepository.findByName(name).isPresent();
    }

    public void preloadDefaultTemplates() {
        if (!templateExists("欢迎邮件")) {
            createTemplate("欢迎邮件", 
                "欢迎加入 {{company}}",
                "亲爱的 {{username}}，\n\n欢迎加入 {{company}}！\n\n您的注册邮箱：{{email}}\n注册时间：{{date}}\n\n如有任何问题，请联系我们。\n\n此致\n{{company}} 团队");
        }

        if (!templateExists("生日祝福")) {
            createTemplate("生日祝福",
                "生日快乐，{{username}}！",
                "亲爱的 {{username}}，\n\n今天是您的生日，祝您生日快乐！\n\n愿新的一岁健康快乐，万事如意！\n\n{{company}} 团队敬上");
        }

        if (!templateExists("系统通知")) {
            createTemplate("系统通知",
                "【{{company}}】系统通知",
                "尊敬的用户，\n\n{{content}}\n\n如有疑问，请联系客服。\n\n{{company}}");
        }
    }
}