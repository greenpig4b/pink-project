package com.pinkproject.transaction.TransactionResponse;

public record _DeleteTransactionRespRecord(
        Integer userId,
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        DailyDeleteTransactionRecord dailyDeleteTransactionRecord,
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
