package com.example.sulmuro_app.dto.test.response;

import lombok.Getter;

@Getter
public class TestResponse {
    private final String message;

    public TestResponse(String name) {
        this.message = String.format("%s이(가) 저장되었습니다.", name);
    }

}
