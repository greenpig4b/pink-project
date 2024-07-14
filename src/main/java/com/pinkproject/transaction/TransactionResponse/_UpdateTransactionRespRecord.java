package com.pinkproject.transaction.TransactionResponse;

import com.pinkproject.transaction.enums.TransactionType;

import java.util.List;

public record _UpdateTransactionRespRecord(
        Integer userId,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        List<DailyUpdateTransactionRecord> dailyUpdateTransactionRecords
) {
    public record DailyUpdateTransactionRecord(
            String date,
            String dailyIncome,
            String dailyExpense,
            String dailyTotalAmount,
            DailyUpdateTransactionDetailRecord dailyUpdateTransactionDetailRecord
    ){
        public record DailyUpdateTransactionDetailRecord(
                Integer id,
                TransactionType transactionType,
                String categoryIn,
                String categoryOut,
                String description,
                String time,
                String assets,
                String amount
        ){}
    }
}
