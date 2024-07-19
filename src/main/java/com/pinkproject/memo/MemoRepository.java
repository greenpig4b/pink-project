package com.pinkproject.memo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Integer> {
    List<Memo> findByUserIdAndCreatedAtBetween(Integer userId, LocalDateTime startDate, LocalDateTime endDate);

    // id로 메모를 조회해 내림차순으로 정렬
    List<Memo> findAllByUserIdOrderByCreatedAtDesc(Integer userId);
}
