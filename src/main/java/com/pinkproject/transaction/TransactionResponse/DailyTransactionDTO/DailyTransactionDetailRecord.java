package com.pinkproject.transaction.TransactionResponse.DailyTransactionDTO;

import com.pinkproject.transaction.enums.TransactionType;

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
