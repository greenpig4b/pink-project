package com.pinkproject.transaction.TransactionResponse.DailyTransactionDTO;

import java.util.List;

public record _DailyMainDTORecord(
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        List<DailyTransaction> dailyTransaction
) {
}
