package com.pinkproject.record.RecordResponse.DailyRecordsDTO;

import java.util.List;

public record _DailyMainDTORecord(
        String month,
        Integer monthlyIncome,
        Integer monthlyExpense,
        Integer monthlyTotalAmount,
        List<DailyRecord> dailyRecord
) {
}
