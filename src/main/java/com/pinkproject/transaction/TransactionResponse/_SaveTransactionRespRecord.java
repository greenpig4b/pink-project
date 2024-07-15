package com.pinkproject.transaction.TransactionResponse;

import com.pinkproject.transaction.enums.TransactionType;

import java.util.List;

public record _SaveTransactionRespRecord(
        Integer userId,
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        List<DailySaveTransactionRecord> dailySaveTransactionRecords
) {
    public record DailySaveTransactionRecord(
            String date,
            String dailyIncome,
            String dailyExpense,
            String dailyTotalAmount,
            DailySaveTransactionDetailRecord dailySaveTransactionDetailRecord
    ){
        public record DailySaveTransactionDetailRecord(
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
