package com.pinkproject.transaction.TransactionResponse;

import com.pinkproject.transaction.enums.TransactionType;

import java.util.List;

public record _MonthlyTransactionMainRecord(
        Integer userId,
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        List<DailyTransactionRecord> dailyTransactionRecord
) {
    public record DailyTransactionRecord(
            String date,
            String dailyIncome,
            String dailyExpense,
            String dailyTotalAmount,
            List<DailyTransactionDetailRecord> dailyTransactionDetailRecord
    ) {
        public record DailyTransactionDetailRecord(
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
