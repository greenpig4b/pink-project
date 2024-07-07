package com.pinkproject.record.RecordResponse.DailyRecordsDTO;

import com.pinkproject.record.enums.Assets;
import com.pinkproject.record.enums.CategoryIn;
import com.pinkproject.record.enums.CategoryOut;
import com.pinkproject.record.enums.TransactionType;

public record DailyTransactionDetail(
        TransactionType transactionType,
        CategoryIn categoryIn,
        CategoryOut categoryOut,
        String description,
        String date,
        String time,
        Assets assets,
        Integer amount
) {
}
