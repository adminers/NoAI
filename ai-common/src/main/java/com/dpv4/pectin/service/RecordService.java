package com.dpv4.pectin.service;

import com.dpv4.pectin.model.MailScheduleRecord;
import com.dpv4.pectin.model.MonthlyStats;
import com.dpv4.pectin.model.PageResult;
import com.dpv4.pectin.repository.MailRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecordService {

    @Autowired
    private MailRecordRepository recordRepository;

    public PageResult<MailScheduleRecord> findByMonth(int year, int month, int page, int pageSize) {
        LocalDateTime startOfMonth = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        List<MailScheduleRecord> allRecords = recordRepository.findByTimeRange(startOfMonth, endOfMonth);
        
        long totalElements = allRecords.size();
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allRecords.size());
        
        List<MailScheduleRecord> pageContent = fromIndex < allRecords.size() 
                ? allRecords.subList(fromIndex, toIndex) 
                : Collections.emptyList();

        return new PageResult<>(pageContent, page, pageSize, totalElements);
    }

    public PageResult<MailScheduleRecord> findByStatusAndMonth(boolean success, int year, int month, int page, int pageSize) {
        LocalDateTime startOfMonth = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        List<MailScheduleRecord> allRecords = recordRepository.findByTimeRange(startOfMonth, endOfMonth);
        
        List<MailScheduleRecord> filteredRecords = allRecords.stream()
                .filter(record -> record.isSuccess() == success)
                .sorted(Comparator.comparing(MailScheduleRecord::getActualSendTime).reversed())
                .collect(Collectors.toList());
        
        long totalElements = filteredRecords.size();
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filteredRecords.size());
        
        List<MailScheduleRecord> pageContent = fromIndex < filteredRecords.size() 
                ? filteredRecords.subList(fromIndex, toIndex) 
                : Collections.emptyList();

        return new PageResult<>(pageContent, page, pageSize, totalElements);
    }

    public List<MonthlyStats> getMonthlyStats(int year) {
        List<MonthlyStats> statsList = new ArrayList<>();
        
        for (int month = 1; month <= 12; month++) {
            LocalDateTime startOfMonth = YearMonth.of(year, month).atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);
            
            List<MailScheduleRecord> records = recordRepository.findByTimeRange(startOfMonth, endOfMonth);
            
            long total = records.size();
            long success = records.stream().filter(MailScheduleRecord::isSuccess).count();
            
            if (total > 0) {
                statsList.add(new MonthlyStats(YearMonth.of(year, month), total, success));
            }
        }
        
        return statsList;
    }

    public MonthlyStats getMonthStats(int year, int month) {
        LocalDateTime startOfMonth = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);
        
        List<MailScheduleRecord> records = recordRepository.findByTimeRange(startOfMonth, endOfMonth);
        
        long total = records.size();
        long success = records.stream().filter(MailScheduleRecord::isSuccess).count();
        
        return new MonthlyStats(YearMonth.of(year, month), total, success);
    }

    public List<Integer> getAvailableYears() {
        List<MailScheduleRecord> allRecords = recordRepository.findAll();
        
        return allRecords.stream()
                .map(record -> record.getCreatedAt().getYear())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public List<Integer> getAvailableMonths(int year) {
        List<MailScheduleRecord> allRecords = recordRepository.findAll();
        
        return allRecords.stream()
                .filter(record -> record.getCreatedAt().getYear() == year)
                .map(record -> record.getCreatedAt().getMonthValue())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public MailScheduleRecord getById(String id) {
        return recordRepository.findById(id).orElse(null);
    }

    public long getTotalCount() {
        return recordRepository.findAll().size();
    }

    public long getSuccessCount() {
        return recordRepository.countByStatus(true);
    }

    public long getFailureCount() {
        return recordRepository.countByStatus(false);
    }
}