package com.pinkproject.record.RecordResponse.DailyRecordsDTO;

public record DailyRecord(
        String date,
        String day,
        String yearMonth,
        Integer dailyIncome,
        Integer dailyExpense,
        Integer dailyTotalAmount,
        DailyIncomeHistory dailyIncomeHistory
) {
}
