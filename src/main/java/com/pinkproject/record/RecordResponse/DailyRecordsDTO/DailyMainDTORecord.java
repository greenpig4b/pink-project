package com.pinkproject.record.RecordResponse.DailyRecordsDTO;

public record DailyMainDTORecord(
        String month,
        Integer monthlyIncome,
        Integer monthlyExpense,
        Integer monthlyTotalAmount,
        DailyRecord dailyRecord
) {
}
