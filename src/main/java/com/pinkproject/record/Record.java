package com.pinkproject.record;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Table(name = "record_tb") // 기록테이블
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    Integer user; // user_id // 유저

    String accountType; // 수입/지출
    String categoryIn; // 월급 등의 소득
    String categoryOut; // 경조사 / 정기 지출 등
    Integer amount; // 금액
    String description; // 지출 / 소비 설명

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성날짜

    @Builder
    public Record(Integer id, Integer user, String accountType, String categoryIn, String categoryOut, Integer amount, String description, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.accountType = accountType;
        this.categoryIn = categoryIn;
        this.categoryOut = categoryOut;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
    }
}
