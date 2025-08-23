package com.example.sulmuro_app.domain.bin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum PlaceCategory {
    ATM("ATM"),
    FOOD("Food"),
    CLOTH("Clothes"),
    GOODS("Goods"),
    CAFE("Cafe"),
    BAR("Bar");

    private final String displayName;

    PlaceCategory(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static PlaceCategory forValue(String value) {
        return Arrays.stream(PlaceCategory.values())
                .filter(c -> c.name().equalsIgnoreCase(value)   // DB 저장용 (대문자 키워드)
                        || c.displayName.equalsIgnoreCase(value)) // 사람이 입력한 경우
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "유효하지 않는 카테고리: " + value + ". 허용된 카테고리: " + Arrays.toString(values())));
    }
}
