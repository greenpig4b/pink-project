package com.pinkproject.transaction.TransactionResponse.DailyTransactionDTO;

import java.util.List;

public record DailyTransactionRecord(
        String date,
        String dailyIncome,
        String dailyExpense,
        String dailyTotalAmount,
        List<DailyTransactionDetailRecord> dailyTransactionDetailRecord
) {
}
