package com.pinkproject.user;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class SessionUser {
    private Integer id; // 유저 번호
    private String email; // 이메일
    private LocalDateTime createdAt; // 유저 가입 일자

    @Builder
    public SessionUser(Integer id, String email, LocalDateTime createdAt) {
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