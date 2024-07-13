package com.pinkproject.memo.MemoResponse;

import java.util.List;

public record _MonthlyMemoMainRecord(
        Integer userId,
        List<DailyMemoRecords> dailyMemoRecordList
) {
    public record DailyMemoRecords(
            String date,
            List<DailyMemoRecord> dailyMemoRecordList
    ){
        public record DailyMemoRecord(
                Integer id,
                String title,
                String content
        ){
        }
    }
}
