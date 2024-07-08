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
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private CategoryIn categoryIn; // 월급 등의 소득

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private CategoryOut categoryOut; // 경조사 / 정기 지출 등

    @Column(nullable = false)
    private Integer amount; // 금액

    @Column(nullable = false)
    private String description; // 지출 / 소비 설명

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성날짜

    @Builder
    public Transaction(Integer id, User user, TransactionType transactionType, Assets assets, CategoryIn categoryIn, CategoryOut categoryOut, Integer amount, String description, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.transactionType = transactionType;
        this.assets = assets;
        this.categoryIn = categoryIn;
        this.categoryOut = categoryOut;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
    }
}
