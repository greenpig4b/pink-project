package com.pinkproject.admin;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@NoArgsConstructor
@Data
@Table(name = "admin_tb") // 관리자 테이블
@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 유저 번호
    private String username; // 유저네임
    private String password; // 패스워드

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성날짜

    @Builder
    public Admin(Integer id, String username, String password, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
    }
}

