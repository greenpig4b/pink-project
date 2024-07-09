package com.pinkproject.memo.MemoRequest;

public record _SaveMemoRecord(
        Integer userId,
        String title,
        String content
) {
}
