package com.pinkproject.notice.noticeRequest;

import java.time.LocalDate;

public record _DetailNoticeAdminRecord(
        Integer id,
        String title,
        String content,
        String username,
        LocalDate createdAt
) {
    public String date() {
        return createdAt.toString(); // LocalDate를 String으로 변환
    }
}
