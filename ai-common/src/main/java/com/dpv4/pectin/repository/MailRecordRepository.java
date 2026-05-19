package com.dpv4.pectin.repository;

import com.dpv4.pectin.model.MailScheduleRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MailRecordRepository {

    MailScheduleRecord save(MailScheduleRecord record);

    Optional<MailScheduleRecord> findById(String id);

    List<MailScheduleRecord> findByStatus(boolean success);

    List<MailScheduleRecord> findByTimeRange(LocalDateTime start, LocalDateTime end);

    List<MailScheduleRecord> findAll();

    List<MailScheduleRecord> findRecent(int limit);

    long countByStatus(boolean success);

    void deleteById(String id);

    void deleteOldRecords(LocalDateTime before);
}