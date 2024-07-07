package com.pinkproject.record.RecordResponse.DailyRecordsDTO;

import com.pinkproject.record.enums.AccountType;
import com.pinkproject.record.enums.CategoryIn;

public record DailyIncomeHistory(
        AccountType accountType,
        String imagePath,
        CategoryIn categoryIn,
        String description,
        String createdAt,
        Integer amount
) {
}
