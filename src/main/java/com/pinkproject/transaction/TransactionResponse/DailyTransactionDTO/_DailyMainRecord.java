package com.pinkproject.transaction.TransactionResponse.DailyTransactionDTO;

import java.util.List;

public record _DailyMainRecord(
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        List<DailyTransactionRecord> dailyTransactionRecord
) {
}
