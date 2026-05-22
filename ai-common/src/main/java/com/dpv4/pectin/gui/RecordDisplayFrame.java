package com.dpv4.pectin.gui;

import com.dpv4.pectin.model.MailScheduleRecord;
import com.dpv4.pectin.model.MonthlyStats;
import com.dpv4.pectin.model.PageResult;
import com.dpv4.pectin.service.RecordService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecordDisplayFrame extends JFrame {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private RecordService recordService;
    
    private JComboBox<Integer> yearCombo;
    private JComboBox<Integer> monthCombo;
    private JComboBox<String> statusFilterCombo;
    
    private JTable recordTable;
    private DefaultTableModel tableModel;
    
    private JLabel pageInfoLabel;
    private JButton firstPageBtn;
    private JButton prevPageBtn;
    private JButton nextPageBtn;
    private JButton lastPageBtn;
    private JTextField pageInput;
    
    private JLabel statsLabel;
    private JLabel successRateLabel;
    
    private int currentPage = 0;
    private int pageSize = 10;
    private int currentYear;
    private int currentMonth;
    private String currentStatusFilter = "all";
    
    public RecordDisplayFrame(RecordService recordService) {
        this.recordService = recordService;
        initUI();
        loadYears();
    }
    
    private void initUI() {
        setTitle("邮件发送记录");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        JPanel statsPanel = createStatsPanel();
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        panel.add(new JLabel("年份:"));
        yearCombo = new JComboBox<>();
        yearCombo.setPreferredSize(new Dimension(100, 25));
        yearCombo.addActionListener(e -> loadMonths());
        panel.add(yearCombo);
        
        panel.add(new JLabel("月份:"));
        monthCombo = new JComboBox<>();
        monthCombo.setPreferredSize(new Dimension(100, 25));
        monthCombo.addActionListener(e -> loadRecords());
        panel.add(monthCombo);
        
        panel.add(new JLabel("状态:"));
        statusFilterCombo = new JComboBox<>(new String[]{"全部", "成功", "失败"});
        statusFilterCombo.setPreferredSize(new Dimension(100, 25));
        statusFilterCombo.addActionListener(e -> loadRecords());
        panel.add(statusFilterCombo);
        
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> loadRecords());
        panel.add(refreshBtn);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("统计信息"));
        
        statsLabel = new JLabel("总计: 0 | 成功: 0 | 失败: 0");
        panel.add(statsLabel);
        
        successRateLabel = new JLabel("成功率: 0%");
        successRateLabel.setForeground(new Color(0, 128, 0));
        panel.add(successRateLabel);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"ID", "收件人", "主题", "状态", "发送时间", "耗时(ms)", "重试次数", "错误信息"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recordTable = new JTable(tableModel);
        recordTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        recordTable.getColumnModel().getColumn(0).setMaxWidth(80);
        recordTable.getColumnModel().getColumn(3).setMaxWidth(80);
        recordTable.getColumnModel().getColumn(5).setMaxWidth(100);
        recordTable.getColumnModel().getColumn(6).setMaxWidth(100);
        
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("成功".equals(value)) {
                    label.setForeground(new Color(0, 128, 0));
                    label.setText("✓ 成功");
                } else if ("失败".equals(value)) {
                    label.setForeground(Color.RED);
                    label.setText("✗ 失败");
                }
                return label;
            }
        };
        recordTable.getColumnModel().getColumn(3).setCellRenderer(statusRenderer);
        
        JScrollPane scrollPane = new JScrollPane(recordTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        firstPageBtn = new JButton("首页");
        firstPageBtn.addActionListener(e -> goToPage(0));
        panel.add(firstPageBtn);
        
        prevPageBtn = new JButton("上一页");
        prevPageBtn.addActionListener(e -> goToPage(currentPage - 1));
        panel.add(prevPageBtn);
        
        pageInfoLabel = new JLabel("第 1/1 页");
        panel.add(pageInfoLabel);
        
        pageInput = new JTextField(5);
        panel.add(pageInput);
        
        JButton goBtn = new JButton("跳转");
        goBtn.addActionListener(e -> goToPage(Integer.parseInt(pageInput.getText()) - 1));
        panel.add(goBtn);
        
        nextPageBtn = new JButton("下一页");
        nextPageBtn.addActionListener(e -> goToPage(currentPage + 1));
        panel.add(nextPageBtn);
        
        lastPageBtn = new JButton("末页");
        lastPageBtn.addActionListener(e -> goToLastPage());
        panel.add(lastPageBtn);
        
        return panel;
    }
    
    private void loadYears() {
        List<Integer> years = recordService.getAvailableYears();
        
        if (years.isEmpty()) {
            int currentYear = LocalDateTime.now().getYear();
            years.add(currentYear);
        }
        
        yearCombo.removeAllItems();
        for (Integer year : years) {
            yearCombo.addItem(year);
        }
        
        if (!years.isEmpty()) {
            currentYear = years.get(0);
            loadMonths();
        }
    }
    
    private void loadMonths() {
        Integer year = (Integer) yearCombo.getSelectedItem();
        if (year != null) {
            currentYear = year;
            List<Integer> months = recordService.getAvailableMonths(year);
            
            if (months.isEmpty()) {
                for (int i = 12; i >= 1; i--) {
                    months.add(i);
                }
            }
            
            monthCombo.removeAllItems();
            for (Integer month : months) {
                monthCombo.addItem(month);
            }
            
            if (!months.isEmpty()) {
                currentMonth = months.get(0);
                loadRecords();
            }
        }
    }
    
    private void loadRecords() {
        Integer year = (Integer) yearCombo.getSelectedItem();
        Integer month = (Integer) monthCombo.getSelectedItem();
        
        if (year == null || month == null) {
            return;
        }
        
        currentYear = year;
        currentMonth = month;
        currentStatusFilter = statusFilterCombo.getSelectedItem().toString();
        
        PageResult<MailScheduleRecord> pageResult;
        
        if ("成功".equals(currentStatusFilter)) {
            pageResult = recordService.findByStatusAndMonth(true, year, month, currentPage, pageSize);
        } else if ("失败".equals(currentStatusFilter)) {
            pageResult = recordService.findByStatusAndMonth(false, year, month, currentPage, pageSize);
        } else {
            pageResult = recordService.findByMonth(year, month, currentPage, pageSize);
        }
        
        updateTable(pageResult);
        updateStats(year, month);
        updatePageInfo(pageResult);
    }
    
    private void updateTable(PageResult<MailScheduleRecord> pageResult) {
        tableModel.setRowCount(0);
        
        for (MailScheduleRecord record : pageResult.getContent()) {
            Object[] row = {
                record.getId().substring(0, 8),
                record.getTo(),
                record.getSubject(),
                record.isSuccess() ? "成功" : "失败",
                record.getActualSendTime() != null ? record.getActualSendTime().format(DATE_FORMATTER) : "-",
                record.getDurationMs(),
                record.getRetryCount(),
                record.getErrorMessage() != null ? record.getErrorMessage() : "-"
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateStats(int year, int month) {
        MonthlyStats stats = recordService.getMonthStats(year, month);
        
        statsLabel.setText(String.format("总计: %d | 成功: %d | 失败: %d",
                stats.getTotalCount(), stats.getSuccessCount(), stats.getFailureCount()));
        
        String rateColor = stats.getSuccessRate() >= 90 ? "#008000" : 
                          stats.getSuccessRate() >= 70 ? "#FFA500" : "#FF0000";
        successRateLabel.setText(String.format("<html>成功率: <font color='%s'>%.1f%%</font></html>", 
                rateColor, stats.getSuccessRate()));
    }
    
    private void updatePageInfo(PageResult<MailScheduleRecord> pageResult) {
        currentPage = pageResult.getPageNumber();
        pageInfoLabel.setText(String.format("第 %d/%d 页", currentPage + 1, pageResult.getTotalPages()));
        
        firstPageBtn.setEnabled(!pageResult.isFirst());
        prevPageBtn.setEnabled(!pageResult.isFirst());
        nextPageBtn.setEnabled(!pageResult.isLast());
        lastPageBtn.setEnabled(!pageResult.isLast());
    }
    
    private void goToPage(int page) {
        if (page >= 0) {
            currentPage = page;
            loadRecords();
        }
    }
    
    private void goToLastPage() {
        PageResult<MailScheduleRecord> pageResult;
        
        if ("成功".equals(currentStatusFilter)) {
            pageResult = recordService.findByStatusAndMonth(true, currentYear, currentMonth, 0, pageSize);
        } else if ("失败".equals(currentStatusFilter)) {
            pageResult = recordService.findByStatusAndMonth(false, currentYear, currentMonth, 0, pageSize);
        } else {
            pageResult = recordService.findByMonth(currentYear, currentMonth, 0, pageSize);
        }
        
        goToPage(pageResult.getTotalPages() - 1);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RecordService recordService = new RecordService();
            RecordDisplayFrame frame = new RecordDisplayFrame(recordService);
            frame.setVisible(true);
        });
    }
}