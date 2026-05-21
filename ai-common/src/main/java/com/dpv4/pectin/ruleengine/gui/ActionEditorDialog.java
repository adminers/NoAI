package com.dpv4.pectin.ruleengine.gui;

import com.dpv4.pectin.ruleengine.model.Action;

import javax.swing.*;
import java.awt.*;

public class ActionEditorDialog extends JDialog {
    
    private Action action;
    private boolean okClicked = false;
    
    private JComboBox<Action.ActionType> typeCombo;
    private JTextField parameterField;
    private JTextArea descriptionArea;
    
    public ActionEditorDialog(JDialog parent, Action action) {
        super(parent, action == null ? "添加动作" : "编辑动作", true);
        this.action = action != null ? action : new Action();
        initUI();
    }
    
    private void initUI() {
        setSize(400, 280);
        setLocationRelativeTo(getParent());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 5, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("动作类型:"), gbc);
        
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(Action.ActionType.values());
        typeCombo.setPreferredSize(new Dimension(250, 25));
        typeCombo.addActionListener(e -> updateParameterHint());
        mainPanel.add(typeCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("参数:"), gbc);
        
        gbc.gridx = 1;
        parameterField = new JTextField(25);
        mainPanel.add(parameterField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("描述:"), gbc);
        
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 25);
        descriptionArea.setLineWrap(true);
        mainPanel.add(new JScrollPane(descriptionArea), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("确定");
        okBtn.addActionListener(e -> saveAction());
        buttonPanel.add(okBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);
        
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
        
        if (action.getType() != null) {
            loadActionData();
        }
    }
    
    private void updateParameterHint() {
        Action.ActionType type = (Action.ActionType) typeCombo.getSelectedItem();
        switch (type) {
            case SEND_EMAIL:
                parameterField.setText("to@example.com|主题|内容");
                break;
            case SEND_EMAIL_WITH_ATTACHMENT:
                parameterField.setText("to@example.com|主题|内容|附件路径");
                break;
            case DELAY_SEND:
                parameterField.setText("延迟毫秒数");
                break;
            case CANCEL_PENDING:
                parameterField.setText("任务ID或条件");
                break;
            case LOG_MESSAGE:
                parameterField.setText("日志消息内容");
                break;
            case EXECUTE_SCRIPT:
                parameterField.setText("脚本路径或内容");
                break;
        }
    }
    
    private void loadActionData() {
        typeCombo.setSelectedItem(action.getType());
        parameterField.setText(action.getParameter());
        descriptionArea.setText(action.getDescription());
    }
    
    private void saveAction() {
        action.setType((Action.ActionType) typeCombo.getSelectedItem());
        action.setParameter(parameterField.getText().trim());
        action.setDescription(descriptionArea.getText().trim());
        
        okClicked = true;
        dispose();
    }
    
    public Action getAction() {
        return action;
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
}