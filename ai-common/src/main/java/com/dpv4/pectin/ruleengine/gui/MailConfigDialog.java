package com.dpv4.pectin.ruleengine.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class MailConfigDialog extends JDialog {
    
    private JTextField hostField;
    private JTextField portField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fromField;
    private JCheckBox authCheckBox;
    private JCheckBox starttlsCheckBox;
    private JCheckBox enabledCheckBox;
    
    public MailConfigDialog(JFrame parent) {
        super(parent, "邮件服务器配置", true);
        initUI();
        loadConfig();
    }
    
    private void initUI() {
        setSize(450, 350);
        setLocationRelativeTo(getParent());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 10, 5, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("SMTP服务器:"), gbc);
        
        gbc.gridx = 1;
        hostField = new JTextField(25);
        mainPanel.add(hostField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("端口:"), gbc);
        
        gbc.gridx = 1;
        portField = new JTextField("587", 25);
        mainPanel.add(portField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("用户名:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(25);
        mainPanel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("密码:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(25);
        mainPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("发件人地址:"), gbc);
        
        gbc.gridx = 1;
        fromField = new JTextField(25);
        mainPanel.add(fromField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        authCheckBox = new JCheckBox("启用SMTP认证");
        authCheckBox.setSelected(true);
        mainPanel.add(authCheckBox, gbc);
        
        gbc.gridy = 6;
        starttlsCheckBox = new JCheckBox("启用STARTTLS");
        starttlsCheckBox.setSelected(true);
        mainPanel.add(starttlsCheckBox, gbc);
        
        gbc.gridy = 7;
        enabledCheckBox = new JCheckBox("启用邮件服务");
        enabledCheckBox.setSelected(false);
        mainPanel.add(enabledCheckBox, gbc);
        
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("保存配置");
        okBtn.addActionListener(e -> saveConfig());
        buttonPanel.add(okBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);
        
        JButton testBtn = new JButton("测试连接");
        testBtn.addActionListener(e -> testConnection());
        buttonPanel.add(testBtn);
        
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    private void loadConfig() {
        try {
            File configFile = new File("application.properties");
            if (configFile.exists()) {
                Properties props = new Properties();
                props.load(new java.io.FileInputStream(configFile));
                
                hostField.setText(props.getProperty("mail.smtp.host", "smtp.gmail.com"));
                portField.setText(props.getProperty("mail.smtp.port", "587"));
                usernameField.setText(props.getProperty("mail.smtp.username", ""));
                passwordField.setText(props.getProperty("mail.smtp.password", ""));
                fromField.setText(props.getProperty("mail.from", ""));
                authCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("mail.smtp.auth", "true")));
                starttlsCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("mail.smtp.starttls", "true")));
                enabledCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("mail.enabled", "false")));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "加载配置失败: " + e.getMessage());
        }
    }
    
    private void saveConfig() {
        try {
            FileWriter writer = new FileWriter("application.properties");
            
            writer.write("# Mail Configuration\n");
            writer.write("mail.smtp.host=" + hostField.getText() + "\n");
            writer.write("mail.smtp.port=" + portField.getText() + "\n");
            writer.write("mail.smtp.username=" + usernameField.getText() + "\n");
            writer.write("mail.smtp.password=" + new String(passwordField.getPassword()) + "\n");
            writer.write("mail.from=" + fromField.getText() + "\n");
            writer.write("mail.smtp.auth=" + authCheckBox.isSelected() + "\n");
            writer.write("mail.smtp.starttls=" + starttlsCheckBox.isSelected() + "\n");
            writer.write("mail.enabled=" + enabledCheckBox.isSelected() + "\n");
            
            writer.close();
            JOptionPane.showMessageDialog(this, "配置保存成功");
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存配置失败: " + e.getMessage());
        }
    }
    
    private void testConnection() {
        JOptionPane.showMessageDialog(this, "测试功能需要实际SMTP服务器连接，当前为演示模式");
    }
}