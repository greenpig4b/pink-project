package com.pinkproject.memo.MemoRequest;

public record _UpdateMemoRecord(
        Integer id,
        Integer userId,
        String title,
        String content
) {
}
