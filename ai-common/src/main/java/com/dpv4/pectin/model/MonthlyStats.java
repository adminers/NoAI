package com.dpv4.pectin.model;

import java.time.YearMonth;
import java.util.List;

public class MonthlyStats {
    
    private YearMonth yearMonth;
    private long totalCount;
    private long successCount;
    private long failureCount;
    private double successRate;
    
    public MonthlyStats() {}
    
    public MonthlyStats(YearMonth yearMonth, long totalCount, long successCount) {
        this.yearMonth = yearMonth;
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failureCount = totalCount - successCount;
        this.successRate = totalCount > 0 ? (double) successCount / totalCount * 100 : 0;
    }
    
    public YearMonth getYearMonth() {
        return yearMonth;
    }
    
    public void setYearMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }
    
    public long getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
    
    public long getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }
    
    public long getFailureCount() {
        return failureCount;
    }
    
    public void setFailureCount(long failureCount) {
        this.failureCount = failureCount;
    }
    
    public double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }
    
    public String getMonthLabel() {
        return yearMonth.getYear() + "年" + yearMonth.getMonthValue() + "月";
    }
}