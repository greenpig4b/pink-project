package com.pinkproject.admin;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SessionAdmin {
    private Integer id; // 관리자 번호
    private String username; // 로그인 아이디
    private Timestamp createdAt; // 생성 일자

    @Builder
    public SessionAdmin(Integer id, String username, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
    }

    public SessionAdmin(Admin admin) {
        this.id = admin.getId();
        this.username = admin.getUsername();
        this.createdAt = admin.getCreatedAt();
    }


}