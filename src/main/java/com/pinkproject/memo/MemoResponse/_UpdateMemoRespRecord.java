package com.pinkproject.memo.MemoResponse;

public record _UpdateMemoRespRecord(
        Integer id,
        Integer userId,
        String title,
        String content
) {
}
