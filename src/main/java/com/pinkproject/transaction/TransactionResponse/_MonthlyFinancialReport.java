package com.pinkproject.transaction.TransactionResponse;

public record _MonthlyFinancialReport(
        Integer userId,
        String year,
        String month,
        String monthlyIncome,
        String monthlyExpense,
        String monthlyTotalAmount,
        String startYearMonthDate,
        String endYearMonthDate,
        MonthlyExpenseSummary monthlyExpenseSummary,
        MonthlyIncomeSummary monthlyIncomeSummary
) {
    public record MonthlyExpenseSummary(
            String previousMonthExpenseComparison,
            String totalMonthlyExpense,
            String monthlyCardExpense,
            String monthlyCashExpense,
            String monthlyBankExpense
    ){
    }
    public record MonthlyIncomeSummary(
            String previousMonthIncomeComparison,
            String totalMonthlyIncome,
            String monthlyCardIncome,
            String monthlyCashIncome,
            String monthlyBankIncome
    ){
    }
}
