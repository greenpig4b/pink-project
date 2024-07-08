package com.pinkproject.transaction.enums;

public enum CategoryOut {
    FOOD("식비"),
    TRANSPORTATION("교통/차량"),
    CULTURE("문화생활"),
    MART("마트/편의점"),
    FASHION("패션/미용"),
    HOUSEHOLD("생활용품"),
    HOUSING("주거/통신"),
    HEALTH("건강"),
    EDUCATION("교육"),
    EVENTS("경조사/회비"),
    PARENTS("부모님"),
    OTHER("기타");

    private final String korean;

    CategoryOut(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }
}
