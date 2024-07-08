package com.pinkproject.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserIdAndCreatedAtBetween(Integer userId, LocalDateTime startDate, LocalDateTime endDate);
}
