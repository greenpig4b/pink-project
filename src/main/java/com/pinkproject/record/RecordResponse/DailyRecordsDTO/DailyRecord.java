package com.pinkproject.record.RecordResponse.DailyRecordsDTO;

import java.util.List;

public record DailyRecord(
        String date,
        String dailyIncome,
        String dailyExpense,
        String dailyTotalAmount,
        List<DailyTransactionDetail> dailyTransactionDetail
) {
}
