package com.pinkproject.transaction.TransactionResponse.DailyTransactionRecord;

import java.util.List;

public record _DailyTransactionMainRecord(
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        List<DailyTransactionRecord> dailyTransactionRecord
) {
}
