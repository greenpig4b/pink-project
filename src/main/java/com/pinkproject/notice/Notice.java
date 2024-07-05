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
@Table(name = "notice_tb") //공지사항 테이블
@Entity
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 유저 번호

    @ManyToOne(fetch = FetchType.LAZY)
    private Admin admin; // 관리자

    @Column(nullable = false)
    private String content; // 내용

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    private String password; // 패스워드

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성날짜

}