package com.pinkproject.transaction.TransactionResponse;

import com.pinkproject.transaction.enums.TransactionType;

import java.util.List;

public record _MonthlyCalendar(
        Integer userId,
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        List<DailySummary> dailySummaries
) {
    public record DailySummary(
            String date,
            boolean hasMemo,
            String dailyIncome,
            String dailyExpense,
            String dailyTotalAmount,
            DailyDetail dailyDetail
    ) {
        public record DailyDetail(
                String date,
                String yearMonth,
                String day,
                List<Memo> memos,
                List<TransactionDetail> transactionDetails
        ) {
            public record Memo(
                    Integer id,
                    String content
            ) {}

            public record TransactionDetail(
                    Integer id,
                    TransactionType transactionType, // 수입, 지출
                    String category, // 식비, 용돈
                    String description,
                    String assets, // 카드, 현금, 계좌
                    String amount
            ) {}
        }
    }

}
