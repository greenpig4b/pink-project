package com.pinkproject.notice;

import com.pinkproject.admin.Admin;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Table(name = "notice_tb") // 공지사항 테이블
@Entity
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 공지사항 번호

    @JoinColumn(name = "admin_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Admin admin; // 관리자

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String content; // 내용

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성날짜

    @Builder
    public Notice(Integer id, Admin admin, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.admin = admin;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }
}
