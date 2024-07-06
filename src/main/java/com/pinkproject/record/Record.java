package com.pinkproject.record;

import com.pinkproject.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Table(name = "record_tb") // 기록테이블
@Entity
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // user_id // 유저

    private String accountType; // 수입/지출
    private String categoryIn; // 월급 등의 소득
    private String categoryOut; // 경조사 / 정기 지출 등
    private Integer amount; // 금액
    private String description; // 지출 / 소비 설명

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성날짜

    @Builder
    public Record(Integer id, User user, String accountType, String categoryIn, String categoryOut, Integer amount, String description, LocalDateTime createdAt) {
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
