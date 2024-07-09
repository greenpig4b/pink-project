package com.pinkproject.transaction.TransactionResponse.DailyTransactionRecord;

import com.pinkproject.transaction.enums.TransactionType;

import java.util.List;

public record _DailyTransactionMainRecord(
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
                TransactionType transactionType,
                String categoryIn,
                String categoryOut,
                String description,
                String time,
                String assets,
                String amount
        ) {
        }

    }
}
