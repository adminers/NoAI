package com.dpv4.pectin.ruleengine.gui;

import com.dpv4.pectin.ruleengine.model.Action;
import com.dpv4.pectin.ruleengine.model.Condition;
import com.dpv4.pectin.ruleengine.model.Rule;
import com.dpv4.pectin.ruleengine.repository.RuleRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RuleEditorDialog extends JDialog {
    
    private Rule rule;
    private RuleRepository ruleRepository;
    
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JCheckBox enabledCheckBox;
    
    private JTable conditionTable;
    private DefaultTableModel conditionTableModel;
    private JTable actionTable;
    private DefaultTableModel actionTableModel;
    
    public RuleEditorDialog(JFrame parent, Rule rule, RuleRepository ruleRepository) {
        super(parent, rule == null ? "新建规则" : "编辑规则", true);
        this.rule = rule != null ? rule : new Rule();
        this.ruleRepository = ruleRepository;
        initUI();
    }
    
    private void initUI() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createConditionPanel());
        splitPane.setRightComponent(createActionPanel());
        splitPane.setDividerLocation(380);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        if (rule.getName() != null) {
            loadRuleData();
        }
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("规则信息"));
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("规则名称:"), gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(30);
        panel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("描述:"), gbc);
        
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        panel.add(new JScrollPane(descriptionArea), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        enabledCheckBox = new JCheckBox("启用规则");
        enabledCheckBox.setSelected(true);
        panel.add(enabledCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createConditionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("条件列表"));
        
        String[] columnNames = {"类型", "字段", "操作符", "值"};
        conditionTableModel = new DefaultTableModel(columnNames, 0);
        conditionTable = new JTable(conditionTableModel);
        
        JScrollPane scrollPane = new JScrollPane(conditionTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("添加条件");
        addBtn.addActionListener(e -> showConditionEditor(null));
        buttonPanel.add(addBtn);
        
        JButton editBtn = new JButton("编辑条件");
        editBtn.addActionListener(e -> editSelectedCondition());
        buttonPanel.add(editBtn);
        
        JButton deleteBtn = new JButton("删除条件");
        deleteBtn.addActionListener(e -> deleteSelectedCondition());
        buttonPanel.add(deleteBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("动作列表"));
        
        String[] columnNames = {"类型", "参数"};
        actionTableModel = new DefaultTableModel(columnNames, 0);
        actionTable = new JTable(actionTableModel);
        
        JScrollPane scrollPane = new JScrollPane(actionTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("添加动作");
        addBtn.addActionListener(e -> showActionEditor(null));
        buttonPanel.add(addBtn);
        
        JButton editBtn = new JButton("编辑动作");
        editBtn.addActionListener(e -> editSelectedAction());
        buttonPanel.add(editBtn);
        
        JButton deleteBtn = new JButton("删除动作");
        deleteBtn.addActionListener(e -> deleteSelectedAction());
        buttonPanel.add(deleteBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okBtn = new JButton("确定");
        okBtn.addActionListener(e -> saveRule());
        panel.add(okBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        panel.add(cancelBtn);
        
        return panel;
    }
    
    private void loadRuleData() {
        nameField.setText(rule.getName());
        descriptionArea.setText(rule.getDescription());
        enabledCheckBox.setSelected(rule.isEnabled());
        
        loadConditions();
        loadActions();
    }
    
    private void loadConditions() {
        conditionTableModel.setRowCount(0);
        for (Condition condition : rule.getConditions()) {
            Object[] row = {
                condition.getType().name(),
                condition.getField(),
                condition.getOperator().name(),
                condition.getValue()
            };
            conditionTableModel.addRow(row);
        }
    }
    
    private void loadActions() {
        actionTableModel.setRowCount(0);
        for (Action action : rule.getActions()) {
            Object[] row = {
                action.getType().name(),
                action.getParameter()
            };
            actionTableModel.addRow(row);
        }
    }
    
    private void showConditionEditor(Condition condition) {
        ConditionEditorDialog dialog = new ConditionEditorDialog(this, condition);
        dialog.setVisible(true);
        
        if (dialog.isOkClicked()) {
            Condition newCondition = dialog.getCondition();
            
            if (condition == null) {
                rule.addCondition(newCondition);
            } else {
                int index = rule.getConditions().indexOf(condition);
                if (index >= 0) {
                    rule.getConditions().set(index, newCondition);
                }
            }
            loadConditions();
        }
    }
    
    private void editSelectedCondition() {
        int selectedRow = conditionTable.getSelectedRow();
        if (selectedRow >= 0) {
            Condition condition = rule.getConditions().get(selectedRow);
            showConditionEditor(condition);
        }
    }
    
    private void deleteSelectedCondition() {
        int selectedRow = conditionTable.getSelectedRow();
        if (selectedRow >= 0) {
            rule.getConditions().remove(selectedRow);
            loadConditions();
        }
    }
    
    private void showActionEditor(Action action) {
        ActionEditorDialog dialog = new ActionEditorDialog(this, action);
        dialog.setVisible(true);
        
        if (dialog.isOkClicked()) {
            Action newAction = dialog.getAction();
            
            if (action == null) {
                rule.addAction(newAction);
            } else {
                int index = rule.getActions().indexOf(action);
                if (index >= 0) {
                    rule.getActions().set(index, newAction);
                }
            }
            loadActions();
        }
    }
    
    private void editSelectedAction() {
        int selectedRow = actionTable.getSelectedRow();
        if (selectedRow >= 0) {
            Action action = rule.getActions().get(selectedRow);
            showActionEditor(action);
        }
    }
    
    private void deleteSelectedAction() {
        int selectedRow = actionTable.getSelectedRow();
        if (selectedRow >= 0) {
            rule.getActions().remove(selectedRow);
            loadActions();
        }
    }
    
    private void saveRule() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入规则名称");
            return;
        }
        
        rule.setName(nameField.getText().trim());
        rule.setDescription(descriptionArea.getText().trim());
        rule.setEnabled(enabledCheckBox.isSelected());
        
        ruleRepository.save(rule);
        JOptionPane.showMessageDialog(this, "规则保存成功");
        dispose();
    }
}