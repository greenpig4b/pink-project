package com.pinkproject.transaction.TransactionResponse;

import java.util.List;

public record _DeleteTransactionRespRecord(
        Integer userId,
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        List<DailyDeleteTransactionRecord> dailyDeleteTransactionRecords,
        String message
) {
    public record DailyDeleteTransactionRecord(
            String date,
            String dailyIncome,
            String dailyExpense,
            String dailyTotalAmount
    ) {
    }
}
