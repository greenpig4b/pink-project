package com.pinkproject.faq;

import com.pinkproject.admin.enums.FaqEnum;
import com.pinkproject.admin.Admin;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Table(name = "faq_tb")
@Entity
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 자주 묻는 질문 번호

    @JoinColumn(name = "admin_id")  // admin_id foreign key in faq_tb table (admin table)
    @ManyToOne(fetch = FetchType.LAZY)
    private Admin admin; // 관리자 번호

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String content; // 질문 내용

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성 일자


    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // USER, USE, COMMON -> 사용자, 사용법, 일
    private FaqEnum classification;

    @Builder
    public Faq(Admin admin, String title, String content, FaqEnum classification) {
        this.admin = admin;
        this.title = title;
        this.content = content;
        this.classification = classification;
    }
}