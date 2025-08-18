package com.example.sulmuro_app.domain.bin;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Theme {
    FOOD,
    SHOP,
    HISTORY;


    @JsonCreator
    public static Theme forValue(String value) {
        try {
            return Theme.valueOf(value.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않는 카테고리: " + value + ". 허용된 카테고리: " + Arrays.toString(values()));
        }
    }
}
