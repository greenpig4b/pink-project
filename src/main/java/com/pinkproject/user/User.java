package com.pinkproject.user;

import com.pinkproject.user.UserRequest.JoinRecord;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Table(name = "user_tb") // 유저테이블
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 유저 번호

    @Column(nullable = true)
    private String oauthProvider;  // 오어스프로바이더

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt; // 유저 가입 일자

    @Builder
    public User(Integer id, String oauthProvider, String email, String password, LocalDateTime createdAt) {
        this.id = id;
        this.oauthProvider = oauthProvider;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }


    public User(JoinRecord joinRecord) {
        this.email = joinRecord.email();
        this.password = joinRecord.password();
    }
}