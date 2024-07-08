package com.pinkproject.transaction.TransactionResponse.DailyTransactionDTO;

import java.util.List;

public record DailyTransaction(
        String date,
        String dailyIncome,
        String dailyExpense,
        String dailyTotalAmount,
        List<DailyTransactionDetail> dailyTransactionDetail
) {
}
