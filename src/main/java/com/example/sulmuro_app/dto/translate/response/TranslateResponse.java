package com.example.sulmuro_app.dto.translate.response;
import lombok.Getter;

@Getter
public class TranslateResponse {
    private final String translatedText;

    public TranslateResponse(String translatedText) {
        this.translatedText = translatedText;
    }
}