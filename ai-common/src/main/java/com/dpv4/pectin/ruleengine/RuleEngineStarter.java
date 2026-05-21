package com.dpv4.pectin.ruleengine;

import com.dpv4.pectin.ruleengine.engine.RuleEngine;
import com.dpv4.pectin.ruleengine.gui.RuleManagementFrame;
import com.dpv4.pectin.ruleengine.repository.FileRuleRepository;
import com.dpv4.pectin.ruleengine.repository.RuleRepository;

import javax.swing.*;

public class RuleEngineStarter {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RuleRepository ruleRepository = new FileRuleRepository();
            RuleEngine ruleEngine = new RuleEngine();
            
            RuleManagementFrame frame = new RuleManagementFrame(ruleRepository, ruleEngine);
            frame.setVisible(true);
        });
    }
}