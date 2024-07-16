package com.pinkproject.transaction.TransactionResponse;

import com.pinkproject.transaction.enums.TransactionType;

public record _UpdateTransactionRespRecord(
        Integer userId,
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        DailyUpdateTransactionRecord dailyUpdateTransactionRecord
) {
    public record DailyUpdateTransactionRecord(
            String date,
            String dailyIncome,
            String dailyExpense,
            String dailyTotalAmount,
            DailyUpdateTransactionDetailRecord dailyUpdateTransactionDetailRecord
    ) {
        public record DailyUpdateTransactionDetailRecord(
                Integer id,
                TransactionType transactionType,
                String categoryIn,
                String categoryOut,
                String categoryOutImage,
                String description,
                String descriptionImage,
                String time,
                String assets,
                String amount
        ) {
        }
    }
}
