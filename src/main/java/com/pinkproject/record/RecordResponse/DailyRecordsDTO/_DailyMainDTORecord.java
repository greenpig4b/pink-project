package com.pinkproject.record.RecordResponse.DailyRecordsDTO;

import java.util.List;

public record _DailyMainDTORecord(
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        List<DailyRecord> dailyRecord
) {
}
