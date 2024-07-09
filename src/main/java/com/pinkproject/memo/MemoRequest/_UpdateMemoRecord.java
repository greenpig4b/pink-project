package com.pinkproject.memo.MemoRequest;

public record _UpdateMemoRecord(
        Integer userId,
        String title,
        String content
) {
}
