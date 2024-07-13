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
    private Assets assets; // TODO: 이거 사실 좀 애매해요ㅠ 일단은 자산의 형태 CASH, BANK, CARD

    // TransactionType이 수입일 경우 카테고리
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private CategoryIn categoryIn; // 월급 등의 소득

    // TransactionType이 지출일 경우 카테고리
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private CategoryOut categoryOut; // 경조사 / 정기 지출 등

    @Column(nullable = false)
    private Integer amount; // 금액

    @Column(nullable = false)
    private String description; // 지출 / 소비 설명

    private LocalDateTime createdAt; // 생성날짜
    private LocalDateTime updatedAt; // 생성날짜

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        }
    }

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
