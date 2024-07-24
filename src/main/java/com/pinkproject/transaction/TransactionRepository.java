package com.pinkproject.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserIdAndCreatedAtBetween(Integer userId, LocalDateTime startDate, LocalDateTime endDate);

    // 월간 데이터 찾기
    @Query("SELECT t FROM Transaction t WHERE YEAR(t.createdAt) = :year AND MONTH(t.createdAt) = :month AND t.user.id = :userId")
    List<Transaction> findAllByYearAndMonth(
                                    @Param("year") int year,
                                    @Param("month") int month,
                                    @Param("userId") int userId);

    // 주간 데이터 찾기
    @Query("SELECT t FROM Transaction t WHERE YEAR(t.createdAt) = :year AND MONTH(t.createdAt) = :month AND t.createdAt BETWEEN :startDate AND :endDate AND t.user.id = :userId")
    List<Transaction> findAllByYearAndMonthAndWeek(
                                    @Param("year") int year,
                                    @Param("month") int month,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("userId") int userId);

}
