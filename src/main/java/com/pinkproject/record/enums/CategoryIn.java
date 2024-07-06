package com.pinkproject.record.enums;

public enum CategoryIn {
    SALARY("월급"),
    SIDE_INCOME("부수입"),
    ALLOWANCE("용돈"),
    BONUS("상여"),
    FINANCIAL_INCOME("금융소득"),
    OTHER("기타");

    private final String korean;

    CategoryIn(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }
}
