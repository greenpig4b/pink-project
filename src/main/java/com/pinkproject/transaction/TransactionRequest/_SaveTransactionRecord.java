package com.pinkproject.transaction.TransactionRequest;

import com.pinkproject.transaction.enums.Assets;
import com.pinkproject.transaction.enums.CategoryIn;
import com.pinkproject.transaction.enums.CategoryOut;
import com.pinkproject.transaction.enums.TransactionType;

public record _SaveTransactionRecord(
        TransactionType transactionType,
        String yearMonthDate,
        String time,
        Integer amount,
        CategoryIn categoryIn,
        CategoryOut categoryOut,
        Assets assets,
        String description
) {
}
