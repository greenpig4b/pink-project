package com.pinkproject.memo;

import com.pinkproject.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    // TODO: 타이틀
    private String content; // 내용

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성날짜
}
