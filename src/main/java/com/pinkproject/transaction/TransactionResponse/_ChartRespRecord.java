package com.pinkproject.transaction.TransactionResponse;

import lombok.Builder;

import java.util.List;

public record _ChartRespRecord(
        Integer monthCount,
//        Integer weekCount,
        MonthDTO chartMonth,
        WeeklyDTO chartWeekly
) {
    @Builder
    public record MonthDTO(
            List<MonthIcomeDTO> incomeList,
            List<MonthSpendingDTO> spendingList
    ) {
        @Builder
        public record MonthIcomeDTO(
                Integer id,
                String category,
                String amount

        ) {
        }

        @Builder
        public record MonthSpendingDTO(
                Integer id,
                String category,
                String amount

        ) {
        }
    }

    @Builder
    public record WeeklyDTO(
            List<WeekIcomeDTO> incomeList,
            List<WeekSpendingDTO> spendingList
    ) {
        @Builder
        public record WeekIcomeDTO(
                Integer id,
                String category,
                String amount

        ) {
        }

        @Builder
        public record WeekSpendingDTO(
                Integer id,
                String category,
                String amount

        ) {
        }
    }
}
