package com.example.sulmuro_app.domain.place.bin;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum PlaceCategory {
    ATM,
    FOOD,
    Clothes,
    Goods,
    Bar;

    @JsonCreator
    public static PlaceCategory forValue(String value) {
        try {
            return PlaceCategory.valueOf(value.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않는 카테고리: " + value + ". 허용된 카테고리: " + Arrays.toString(values()));
        }
    }
}