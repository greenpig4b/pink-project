package com.pinkproject.memo.MemoResponse;

public record _SaveMemoRespRecord(
        Integer id,
        Integer userId,
        String title,
        String content
) {
}
