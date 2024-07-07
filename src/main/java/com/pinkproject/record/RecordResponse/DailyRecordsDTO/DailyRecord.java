package com.pinkproject.record.RecordResponse.DailyRecordsDTO;

import java.util.List;

public record DailyRecord(
        String date,
        Integer dailyIncome,
        Integer dailyExpense,
        Integer dailyTotalAmount,
        List<DailyTransactionDetail> dailyTransactionDetail
) {
}
