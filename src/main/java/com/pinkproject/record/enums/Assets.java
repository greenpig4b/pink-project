package com.pinkproject.record.enums;

public enum Assets {
    CASH("현금"),
    BANK("은행"),
    CARD("카드");

    private final String korean;

    Assets(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }
}
