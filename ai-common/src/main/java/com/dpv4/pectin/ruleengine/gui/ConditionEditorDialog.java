package com.dpv4.pectin.ruleengine.gui;

import com.dpv4.pectin.ruleengine.model.Condition;

import javax.swing.*;
import java.awt.*;

public class ConditionEditorDialog extends JDialog {
    
    private Condition condition;
    private boolean okClicked = false;
    
    private JComboBox<Condition.ConditionType> typeCombo;
    private JTextField fieldField;
    private JComboBox<Condition.Operator> operatorCombo;
    private JTextField valueField;
    
    public ConditionEditorDialog(JDialog parent, Condition condition) {
        super(parent, condition == null ? "添加条件" : "编辑条件", true);
        this.condition = condition != null ? condition : new Condition();
        initUI();
    }
    
    private void initUI() {
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 5, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("条件类型:"), gbc);
        
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(Condition.ConditionType.values());
        typeCombo.setPreferredSize(new Dimension(200, 25));
        typeCombo.addActionListener(e -> updateFieldHint());
        mainPanel.add(typeCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("字段:"), gbc);
        
        gbc.gridx = 1;
        fieldField = new JTextField(20);
        mainPanel.add(fieldField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("操作符:"), gbc);
        
        gbc.gridx = 1;
        operatorCombo = new JComboBox<>(Condition.Operator.values());
        operatorCombo.setPreferredSize(new Dimension(200, 25));
        mainPanel.add(operatorCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("值:"), gbc);
        
        gbc.gridx = 1;
        valueField = new JTextField(20);
        mainPanel.add(valueField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("确定");
        okBtn.addActionListener(e -> saveCondition());
        buttonPanel.add(okBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);
        
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
        
        if (condition.getType() != null) {
            loadConditionData();
        }
    }
    
    private void updateFieldHint() {
        Condition.ConditionType type = (Condition.ConditionType) typeCombo.getSelectedItem();
        switch (type) {
            case TIME:
                fieldField.setText("time");
                fieldField.setEnabled(false);
                valueField.setText("HH:MM");
                break;
            case DATE:
                fieldField.setText("date");
                fieldField.setEnabled(false);
                valueField.setText("YYYY-MM-DD");
                break;
            case WEEKDAY:
                fieldField.setText("weekday");
                fieldField.setEnabled(false);
                valueField.setText("1-7");
                break;
            case RECIPIENT:
                fieldField.setText("recipient");
                fieldField.setEnabled(false);
                valueField.setText("邮箱地址");
                break;
            case SUBJECT:
                fieldField.setText("subject");
                fieldField.setEnabled(false);
                valueField.setText("主题包含");
                break;
            case CUSTOM:
                fieldField.setEnabled(true);
                fieldField.setText("");
                valueField.setText("");
                break;
        }
    }
    
    private void loadConditionData() {
        typeCombo.setSelectedItem(condition.getType());
        fieldField.setText(condition.getField());
        operatorCombo.setSelectedItem(condition.getOperator());
        valueField.setText(condition.getValue());
    }
    
    private void saveCondition() {
        condition.setType((Condition.ConditionType) typeCombo.getSelectedItem());
        condition.setField(fieldField.getText().trim());
        condition.setOperator((Condition.Operator) operatorCombo.getSelectedItem());
        condition.setValue(valueField.getText().trim());
        
        okClicked = true;
        dispose();
    }
    
    public Condition getCondition() {
        return condition;
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
}