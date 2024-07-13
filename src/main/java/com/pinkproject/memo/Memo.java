package com.pinkproject.memo;

import com.pinkproject.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor
@Data
@Table(name = "memo_tb") //메모 테이블
@Entity
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // user_id // 유저

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String content; // 내용

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
    public Memo(Integer id, User user, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
