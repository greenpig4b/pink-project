package com.pinkproject.transaction.TransactionResponse;

import com.pinkproject.transaction.enums.CategoryIn;
import com.pinkproject.transaction.enums.CategoryOut;
import com.pinkproject.transaction.enums.TransactionType;

public record _SaveTransactionRespRecord(
        Integer userId,
        TransactionType transactionType,
        String yearMonthDate,
        String time,
        String amount,
        CategoryIn categoryIn,
        CategoryOut categoryOut,
        String assets,
        String description
) {
}
