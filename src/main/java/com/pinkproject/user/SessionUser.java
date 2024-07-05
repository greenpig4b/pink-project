package com.pinkproject.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionUser {
    private Integer id; // 유저 번호
    private String email; // 이메일
    private LocalDateTime createdAt; // 유저 가입 일자

    @Builder
    public SessionUser(Integer id, String username, String email, String password, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
    }

    public SessionUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
    }
}