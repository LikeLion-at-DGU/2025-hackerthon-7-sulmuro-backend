package com.example.sulmuro_app.domain.place.bin;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Location {
    GWANGJANG_MARKET;

    @JsonCreator
    public static Location forValue(String value) {
        try {
            return Location.valueOf(value.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않는 장소: " + value + ". 허용된 장소: " + Arrays.toString(values()));
        }
    }
}
