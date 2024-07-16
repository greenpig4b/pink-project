package com.pinkproject.transaction.enums;

public enum CategoryOut {
    FOOD("식비", "\uD83C\uDF71"), // 🍱
    TRANSPORTATION("교통/차량", "\uD83D\uDE96"), // 🚖
    CULTURE("문화생활", "\uD83D\uDDBC\uFE0F"), // 🖼️
    MART("마트/편의점", "\uD83D\uDED2"), // 🛒
    FASHION("패션/미용", "\uD83E\uDDE5"), // 🧥
    HOUSEHOLD("생활용품", "\uD83E\uDE91"), // 🪑
    HOUSING("주거/통신", "\uD83C\uDFE0"), // 🏠
    HEALTH("건강", "\uD83E\uDDD8"), // 🧘
    EDUCATION("교육", "\uD83D\uDCD6"), // 📖
    EVENTS("경조사/회비", "\uD83C\uDF81"), // 🎁
    PARENTS("부모님", "\uD83D\uDC75"), // 👵
    OTHER("기타", "\uD83C\uDFB8"); // 🎸

    private final String korean;
    private final String emoji;

    CategoryOut(String korean, String emoji) {
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
