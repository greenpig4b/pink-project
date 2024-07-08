package com.pinkproject.transaction.enums;

public enum TransactionType {
    INCOME("수입"),
    EXPENSE("지출");

    private final String korean;

    TransactionType(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }
}
