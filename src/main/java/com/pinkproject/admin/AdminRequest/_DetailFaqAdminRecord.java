package com.pinkproject.admin.AdminRequest;

import com.pinkproject.admin.enums.FaqEnum;

import java.time.LocalDate;

public record _DetailFaqAdminRecord(
       Integer id,
       String title,
       String content,
       String username,
       FaqEnum classification,
       LocalDate createdAt
) {
    public String date() {
        return createdAt.toString(); // LocalDate를 String으로 변환
    }
}
