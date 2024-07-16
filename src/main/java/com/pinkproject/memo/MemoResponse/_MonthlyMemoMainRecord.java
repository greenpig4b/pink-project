package com.pinkproject.memo.MemoResponse;

import java.util.List;

public record _MonthlyMemoMainRecord(
        Integer userId,
        String year,
        String month,
        List<DailyMemoRecords> dailyMemoRecords
) {
    public record DailyMemoRecords(
            String date,
            List<DailyMemoRecord> dailyMemoRecordList
    ) {
        public record DailyMemoRecord(
                Integer id,
                String title,
                String content
        ) {
        }
    }
}
