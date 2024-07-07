package com.pinkproject.record;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Integer> {
    List<Record> findByUserIdAndCreatedAtBetween(Integer userId, LocalDateTime startDate, LocalDateTime endDate);
}
