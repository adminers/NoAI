package com.dpv4.pectin.gui;

import com.dpv4.pectin.model.MailTemplate;
import com.dpv4.pectin.repository.MailTemplateRepository;
import com.dpv4.pectin.repository.FileMailTemplateRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TemplateManagementFrame extends JFrame {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private MailTemplateRepository templateRepository;
    
    private JTable templateTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField subjectField;
    private JTextArea contentArea;
    private JTextField placeholderField;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton clearButton;
    
    public TemplateManagementFrame() {
        this.templateRepository = new FileMailTemplateRepository();
        initUI();
        loadTemplates();
    }
    
    private void initUI() {
        setTitle("邮件模板管理");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createListPanel());
        splitPane.setRightComponent(createEditorPanel());
        splitPane.setDividerLocation(350);
        
        add(splitPane);
    }
    
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("模板列表"));
        
        String[] columnNames = {"名称", "主题", "使用次数"};
        tableModel = new DefaultTableModel(columnNames, 0);
        templateTable = new JTable(tableModel);
        templateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        templateTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedTemplate();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(templateTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton newBtn = new JButton("新建");
        newBtn.addActionListener(e -> clearForm());
        buttonPanel.add(newBtn);
        
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> loadTemplates());
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("模板编辑"));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("模板名称:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(25);
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("邮件主题:"), gbc);
        
        gbc.gridx = 1;
        subjectField = new JTextField(25);
        formPanel.add(subjectField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("占位符:"), gbc);
        
        gbc.gridx = 1;
        placeholderField = new JTextField(25);
        formPanel.add(placeholderField, gbc);
        
        JLabel hintLabel = new JLabel("<html><font color='gray'>格式: name,email,date (用逗号分隔)</font></html>");
        gbc.gridx = 2;
        formPanel.add(hintLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("邮件内容:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        contentArea = new JTextArea(12, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        formPanel.add(contentScroll, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("保存模板");
        saveButton.addActionListener(e -> saveTemplate());
        buttonPanel.add(saveButton);
        
        deleteButton = new JButton("删除模板");
        deleteButton.addActionListener(e -> deleteTemplate());
        buttonPanel.add(deleteButton);
        
        clearButton = new JButton("清空表单");
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadTemplates() {
        tableModel.setRowCount(0);
        List<MailTemplate> templates = templateRepository.findAll();
        
        for (MailTemplate template : templates) {
            Object[] row = {
                template.getName(),
                template.getSubject(),
                template.getUsageCount()
            };
            tableModel.addRow(row);
        }
    }
    
    private void loadSelectedTemplate() {
        int selectedRow = templateTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            templateRepository.findByName(name).ifPresent(template -> {
                nameField.setText(template.getName());
                subjectField.setText(template.getSubject());
                contentArea.setText(template.getContent());
                placeholderField.setText(template.getPlaceholders());
            });
        }
    }
    
    private void saveTemplate() {
        String name = nameField.getText().trim();
        String subject = subjectField.getText().trim();
        String content = contentArea.getText().trim();
        String placeholders = placeholderField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入模板名称");
            return;
        }
        
        if (subject.isEmpty() && content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入主题或内容");
            return;
        }
        
        MailTemplate template = templateRepository.findByName(name)
                .orElse(new MailTemplate());
        
        template.setName(name);
        template.setSubject(subject);
        template.setContent(content);
        template.setPlaceholders(placeholders);
        
        templateRepository.save(template);
        JOptionPane.showMessageDialog(this, "模板保存成功");
        loadTemplates();
    }
    
    private void deleteTemplate() {
        int selectedRow = templateTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "确定要删除模板 '" + name + "' 吗？",
                    "确认删除",
                    JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                templateRepository.findByName(name).ifPresent(template -> {
                    templateRepository.deleteById(template.getId());
                    clearForm();
                    loadTemplates();
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要删除的模板");
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        subjectField.setText("");
        contentArea.setText("");
        placeholderField.setText("");
        templateTable.clearSelection();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TemplateManagementFrame frame = new TemplateManagementFrame();
            frame.setVisible(true);
        });
    }
}