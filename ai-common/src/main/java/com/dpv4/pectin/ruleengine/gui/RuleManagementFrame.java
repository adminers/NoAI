package com.dpv4.pectin.ruleengine.gui;

import com.dpv4.pectin.ruleengine.engine.RuleEngine;
import com.dpv4.pectin.ruleengine.model.Rule;
import com.dpv4.pectin.ruleengine.repository.RuleRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RuleManagementFrame extends JFrame {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private RuleRepository ruleRepository;
    private RuleEngine ruleEngine;
    
    private JTable ruleTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton enableButton;
    private JButton executeButton;
    private JButton mailConfigButton;
    private JButton recordsButton;
    
    public RuleManagementFrame(RuleRepository ruleRepository, RuleEngine ruleEngine) {
        this.ruleRepository = ruleRepository;
        this.ruleEngine = ruleEngine;
        initUI();
    }
    
    private void initUI() {
        setTitle("邮件规则引擎管理");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JToolBar toolBar = createToolBar();
        mainPanel.add(toolBar, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        loadRules();
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        addButton = new JButton("新建规则");
        addButton.addActionListener(e -> showRuleEditor(null));
        toolBar.add(addButton);
        
        toolBar.addSeparator();
        
        editButton = new JButton("编辑规则");
        editButton.addActionListener(e -> editSelectedRule());
        toolBar.add(editButton);
        
        deleteButton = new JButton("删除规则");
        deleteButton.addActionListener(e -> deleteSelectedRule());
        toolBar.add(deleteButton);
        
        toolBar.addSeparator();
        
        enableButton = new JButton("启用/禁用");
        enableButton.addActionListener(e -> toggleRuleStatus());
        toolBar.add(enableButton);
        
        toolBar.addSeparator();
        
        executeButton = new JButton("执行规则");
        executeButton.addActionListener(e -> executeRules());
        toolBar.add(executeButton);
        
        toolBar.addSeparator();
        
        mailConfigButton = new JButton("邮件配置");
        mailConfigButton.addActionListener(e -> showMailConfig());
        toolBar.add(mailConfigButton);
        
        toolBar.addSeparator();
        
        recordsButton = new JButton("查看记录");
        recordsButton.addActionListener(e -> showRecords());
        toolBar.add(recordsButton);
        
        return toolBar;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"规则名称", "状态", "条件数", "动作数", "创建时间", "最后执行"};
        tableModel = new DefaultTableModel(columnNames, 0);
        ruleTable = new JTable(tableModel);
        ruleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(ruleTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel statusLabel = new JLabel("规则总数: 0 | 启用: 0");
        panel.add(statusLabel);
        
        return panel;
    }
    
    private void loadRules() {
        tableModel.setRowCount(0);
        
        List<Rule> rules = ruleRepository.findAll();
        
        for (Rule rule : rules) {
            Object[] row = {
                rule.getName(),
                rule.isEnabled() ? "启用" : "禁用",
                rule.getConditions().size(),
                rule.getActions().size(),
                rule.getCreatedAt() != null ? rule.getCreatedAt().format(DATE_FORMATTER) : "-",
                rule.getLastExecuted() != null ? rule.getLastExecuted().format(DATE_FORMATTER) : "-"
            };
            tableModel.addRow(row);
        }
        
        updateStatus();
    }
    
    private void updateStatus() {
        List<Rule> allRules = ruleRepository.findAll();
        long enabledCount = allRules.stream().filter(Rule::isEnabled).count();
        
        JPanel statusPanel = (JPanel) getContentPane().getComponent(2);
        JLabel statusLabel = (JLabel) statusPanel.getComponent(0);
        statusLabel.setText(String.format("规则总数: %d | 启用: %d", allRules.size(), enabledCount));
    }
    
    private void showRuleEditor(Rule rule) {
        RuleEditorDialog dialog = new RuleEditorDialog(this, rule, ruleRepository);
        dialog.setVisible(true);
        loadRules();
    }
    
    private void editSelectedRule() {
        int selectedRow = ruleTable.getSelectedRow();
        if (selectedRow >= 0) {
            String ruleName = (String) tableModel.getValueAt(selectedRow, 0);
            List<Rule> rules = ruleRepository.findAll();
            
            for (Rule rule : rules) {
                if (rule.getName().equals(ruleName)) {
                    showRuleEditor(rule);
                    return;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要编辑的规则");
        }
    }
    
    private void deleteSelectedRule() {
        int selectedRow = ruleTable.getSelectedRow();
        if (selectedRow >= 0) {
            String ruleName = (String) tableModel.getValueAt(selectedRow, 0);
            List<Rule> rules = ruleRepository.findAll();
            
            for (Rule rule : rules) {
                if (rule.getName().equals(ruleName)) {
                    int confirm = JOptionPane.showConfirmDialog(this, 
                        "确定要删除规则 '" + ruleName + "' 吗？", "确认删除", 
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        ruleRepository.deleteById(rule.getId());
                        loadRules();
                    }
                    return;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要删除的规则");
        }
    }
    
    private void toggleRuleStatus() {
        int selectedRow = ruleTable.getSelectedRow();
        if (selectedRow >= 0) {
            String ruleName = (String) tableModel.getValueAt(selectedRow, 0);
            List<Rule> rules = ruleRepository.findAll();
            
            for (Rule rule : rules) {
                if (rule.getName().equals(ruleName)) {
                    rule.setEnabled(!rule.isEnabled());
                    ruleRepository.save(rule);
                    loadRules();
                    return;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要操作的规则");
        }
    }
    
    private void executeRules() {
        ruleEngine.evaluateAsync();
        JOptionPane.showMessageDialog(this, "规则执行已启动");
        loadRules();
    }
    
    private void showMailConfig() {
        MailConfigDialog dialog = new MailConfigDialog(this);
        dialog.setVisible(true);
    }
    
    private void showRecords() {
        com.dpv4.pectin.service.RecordService recordService = new com.dpv4.pectin.service.RecordService();
        com.dpv4.pectin.gui.RecordDisplayFrame frame = new com.dpv4.pectin.gui.RecordDisplayFrame(recordService);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RuleRepository ruleRepository = new com.dpv4.pectin.ruleengine.repository.FileRuleRepository();
            RuleEngine ruleEngine = new RuleEngine();
            RuleManagementFrame frame = new RuleManagementFrame(ruleRepository, ruleEngine);
            frame.setVisible(true);
        });
    }
}