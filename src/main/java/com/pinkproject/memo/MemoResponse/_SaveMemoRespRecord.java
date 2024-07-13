package com.pinkproject.memo.MemoResponse;

public record _SaveMemoRespRecord(
        Integer id,
        Integer userId,
        String monthDateDay, // "MM.dd(요일)" 형식의 날짜
        String title,
        String content
) {
}
