package com.pinkproject.transaction;

import com.pinkproject.transaction.enums.Assets;
import com.pinkproject.transaction.enums.CategoryIn;
import com.pinkproject.transaction.enums.CategoryOut;
import com.pinkproject.transaction.enums.TransactionType;
import com.pinkproject.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor
@Data
@Table(name = "transaction_tb") // 기록테이블
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // user_id // 유저

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // 수입/지출

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Assets assets; // 자산 형태 (CASH, BANK, CARD)

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private CategoryIn categoryIn; // 소득 카테고리

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private CategoryOut categoryOut; // 지출 카테고리

    @Column(nullable = false)
    private Integer amount; // 금액

    @Column(nullable = false)
    private String description; // 설명

    private LocalDateTime createdAt; // 생성 날짜
    private LocalDateTime updatedAt; // 수정 날짜

    @PrePersist
    protected void onCreate() {
        // 엔티티가 처음 저장될 때 호출됨
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); // 생성 날짜를 초 단위로 설정
    }

    @PreUpdate
    protected void onUpdate() {
        // 엔티티가 업데이트될 때 호출됨
        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); // 수정 날짜를 초 단위로 설정
    }

    // 수정 날짜가 있으면 수정 날짜를, 없으면 생성 날짜를 반환
    public LocalDateTime getEffectiveDateTime() {
        return updatedAt != null ? updatedAt : createdAt;
    }

    @Builder
    public Transaction(Integer id, User user, TransactionType transactionType, Assets assets, CategoryIn categoryIn, CategoryOut categoryOut, Integer amount, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.transactionType = transactionType;
        this.assets = assets;
        this.categoryIn = categoryIn;
        this.categoryOut = categoryOut;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt != null ? createdAt.truncatedTo(ChronoUnit.SECONDS) : null;
        this.updatedAt = updatedAt != null ? updatedAt.truncatedTo(ChronoUnit.SECONDS) : null;
    }
}
