package com.pinkproject._core.utils;

import com.pinkproject.transaction.Transaction;
import com.pinkproject.transaction.enums.Assets;
import com.pinkproject.transaction.enums.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SummaryUtil {

    public static class Summary {
        private final int monthlyIncome;
        private final int monthlyExpense;
        private final int monthlyTotalAmount;
        private final Map<Assets, Integer> monthlyIncomeByAsset;
        private final Map<Assets, Integer> monthlyExpenseByAsset;
        private final Map<LocalDate, DailySummary> dailySummaries;

        public Summary(int monthlyIncome, int monthlyExpense, int monthlyTotalAmount, Map<Assets, Integer> monthlyIncomeByAsset, Map<Assets, Integer> monthlyExpenseByAsset, Map<LocalDate, DailySummary> dailySummaries) {
            this.monthlyIncome = monthlyIncome;
            this.monthlyExpense = monthlyExpense;
            this.monthlyTotalAmount = monthlyTotalAmount;
            this.monthlyIncomeByAsset = monthlyIncomeByAsset;
            this.monthlyExpenseByAsset = monthlyExpenseByAsset;
            this.dailySummaries = dailySummaries;
        }

        public int getMonthlyIncome() {
            return monthlyIncome;
        }

        public int getMonthlyExpense() {
            return monthlyExpense;
        }

        public int getMonthlyTotalAmount() {
            return monthlyTotalAmount;
        }

        public int getMonthlyIncomeByAsset(Assets asset) {
            return monthlyIncomeByAsset.getOrDefault(asset, 0);
        }

        public int getMonthlyExpenseByAsset(Assets asset) {
            return monthlyExpenseByAsset.getOrDefault(asset, 0);
        }

        public Map<LocalDate, DailySummary> getDailySummaries() {
            return dailySummaries;
        }
    }

    public static class DailySummary {
        private final int dailyIncome;
        private final int dailyExpense;
        private final int dailyTotalAmount;

        public DailySummary(int dailyIncome, int dailyExpense, int dailyTotalAmount) {
            this.dailyIncome = dailyIncome;
            this.dailyExpense = dailyExpense;
            this.dailyTotalAmount = dailyTotalAmount;
        }

        public int getDailyIncome() {
            return dailyIncome;
        }

        public int getDailyExpense() {
            return dailyExpense;
        }

        public int getDailyTotalAmount() {
            return dailyTotalAmount;
        }
    }

    public static Summary calculateSummary(List<Transaction> transactions) {
        int monthlyIncome = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME)
                .mapToInt(Transaction::getAmount)
                .sum();
        int monthlyExpense = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE)
                .mapToInt(Transaction::getAmount)
                .sum();
        int monthlyTotalAmount = monthlyIncome - monthlyExpense;

        Map<Assets, Integer> monthlyIncomeByAsset = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(Transaction::getAssets, Collectors.summingInt(Transaction::getAmount)));

        Map<Assets, Integer> monthlyExpenseByAsset = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(Transaction::getAssets, Collectors.summingInt(Transaction::getAmount)));

        Map<LocalDate, DailySummary> dailySummaries = transactions.stream()
                .collect(Collectors.groupingBy(transaction -> transaction.getEffectiveDateTime().toLocalDate(),
                        Collectors.collectingAndThen(Collectors.toList(), SummaryUtil::calculateDailySummary)));

        return new Summary(monthlyIncome, monthlyExpense, monthlyTotalAmount, monthlyIncomeByAsset, monthlyExpenseByAsset, dailySummaries);
    }

    public static DailySummary calculateDailySummary(List<Transaction> dailyTransactions) {
        int dailyIncome = dailyTransactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME)
                .mapToInt(Transaction::getAmount)
                .sum();
        int dailyExpense = dailyTransactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE)
                .mapToInt(Transaction::getAmount)
                .sum();
        int dailyTotalAmount = dailyIncome - dailyExpense;

        return new DailySummary(dailyIncome, dailyExpense, dailyTotalAmount);
    }
}
