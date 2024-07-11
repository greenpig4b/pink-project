package com.pinkproject.admin.enums;

public enum FaqEnum {
    USER("User"),
    USE("Use"),
    COMMON("Common");

    private final String value;

    FaqEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FaqEnum fromValue(String value) {
        for (FaqEnum enumValue : FaqEnum.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
