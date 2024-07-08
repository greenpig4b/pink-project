package com.pinkproject.transaction.TransactionRequest.SaveTransactionDTO;

import com.pinkproject.transaction.enums.Assets;
import com.pinkproject.transaction.enums.CategoryIn;
import com.pinkproject.transaction.enums.CategoryOut;
import com.pinkproject.transaction.enums.TransactionType;

public record _SaveTransactionDTO(
        Integer userId,
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
