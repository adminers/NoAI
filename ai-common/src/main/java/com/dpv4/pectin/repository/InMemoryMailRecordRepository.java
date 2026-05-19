package com.dpv4.pectin.repository;

import com.dpv4.pectin.model.MailScheduleRecord;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Repository
public class InMemoryMailRecordRepository implements MailRecordRepository {

    private final ConcurrentHashMap<String, MailScheduleRecord> records = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<MailScheduleRecord> recentRecords = new ConcurrentLinkedDeque<>();
    private static final int MAX_RECENT_RECORDS = 1000;

    @Override
    public MailScheduleRecord save(MailScheduleRecord record) {
        if (record.getId() == null || record.getId().isEmpty()) {
            record.setId(UUID.randomUUID().toString());
        }
        records.put(record.getId(), record);
        
        recentRecords.addFirst(record);
        while (recentRecords.size() > MAX_RECENT_RECORDS) {
            recentRecords.pollLast();
        }
        
        return record;
    }

    @Override
    public Optional<MailScheduleRecord> findById(String id) {
        return Optional.ofNullable(records.get(id));
    }

    @Override
    public List<MailScheduleRecord> findByStatus(boolean success) {
        return records.values().stream()
                .filter(record -> record.isSuccess() == success)
                .sorted(Comparator.comparing(MailScheduleRecord::getActualSendTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<MailScheduleRecord> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        return records.values().stream()
                .filter(record -> {
                    LocalDateTime sendTime = record.getActualSendTime();
                    return sendTime != null && !sendTime.isBefore(start) && !sendTime.isAfter(end);
                })
                .sorted(Comparator.comparing(MailScheduleRecord::getActualSendTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<MailScheduleRecord> findAll() {
        return records.values().stream()
                .sorted(Comparator.comparing(MailScheduleRecord::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<MailScheduleRecord> findRecent(int limit) {
        return recentRecords.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public long countByStatus(boolean success) {
        return records.values().stream()
                .filter(record -> record.isSuccess() == success)
                .count();
    }

    @Override
    public void deleteById(String id) {
        records.remove(id);
    }

    @Override
    public void deleteOldRecords(LocalDateTime before) {
        records.entrySet().removeIf(entry -> {
            LocalDateTime createdAt = entry.getValue().getCreatedAt();
            return createdAt != null && createdAt.isBefore(before);
        });
    }
}