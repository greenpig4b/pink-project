package com.pinkproject.transaction.enums;

public enum CategoryIn {
    SALARY("월급", "\uD83D\uDCB0"), // 💰
    SIDE_INCOME("부수입", "\uD83D\uDCB8"), // 💸
    ALLOWANCE("용돈", "\uD83E\uDD11"), // 🤑
    BONUS("상여", "\uD83C\uDFC5"), // 🏅
    FINANCIAL_INCOME("금융소득", "\uD83C\uDFE6"), // 🏦
    OTHER("기타", "\uD83C\uDFB8"); // 🎸

    private final String korean;
    private final String emoji;

    CategoryIn(String korean, String emoji) {
        this.korean = korean;
        this.emoji = emoji;
    }

    public String getKorean() {
        return korean;
    }

    public String getEmoji() {
        return emoji;
    }
}
