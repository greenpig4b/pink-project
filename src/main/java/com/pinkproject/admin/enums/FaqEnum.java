package com.pinkproject.admin.enums;

public enum FaqEnum {
    USER("로그인"),
    USE("이용문의"),
    COMMON("공통");

    private final String value;

    FaqEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FaqEnum fromValue(String value) {
        for (FaqEnum enumValue : FaqEnum.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value) || enumValue.name().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
