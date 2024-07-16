package com.pinkproject.transaction.TransactionResponse;

import com.pinkproject.transaction.enums.TransactionType;

public record _SaveTransactionRespRecord(
        Integer userId,
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        DailySaveTransactionRecord dailySaveTransactionRecord
) {
    public record DailySaveTransactionRecord(
            String date,
            String dailyIncome,
            String dailyExpense,
            String dailyTotalAmount,
            DailySaveTransactionDetailRecord dailySaveTransactionDetailRecord
    ) {
        public record DailySaveTransactionDetailRecord(
                Integer id,
                TransactionType transactionType,
                String categoryIn,
                String categoryInImage,
                String categoryOut,
                String categoryOutImage,
                String description,
                String time,
                String assets,
                String amount
        ) {
        }
    }
}
