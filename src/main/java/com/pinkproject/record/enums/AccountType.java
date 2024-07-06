package com.pinkproject.record.enums;

public enum AccountType {
    INCOME("수입"),
    EXPENSE("지출");

    private final String korean;

    AccountType(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }
}
