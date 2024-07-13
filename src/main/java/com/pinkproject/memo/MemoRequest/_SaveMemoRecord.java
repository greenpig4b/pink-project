package com.pinkproject.memo.MemoRequest;

public record _SaveMemoRecord(
        Integer userId,
        String yearMonthDate, // "yyyy-MM-dd" 형식의 날짜
        String title,
        String content
) {
}
