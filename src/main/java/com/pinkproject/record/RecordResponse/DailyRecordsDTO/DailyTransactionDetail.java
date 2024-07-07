package com.pinkproject.record.RecordResponse.DailyRecordsDTO;

import com.pinkproject.record.enums.TransactionType;

public record DailyTransactionDetail(
        TransactionType transactionType,
        String categoryIn,
        String categoryOut,
        String description,
        String time,
        String assets,
        String amount
) {
}
