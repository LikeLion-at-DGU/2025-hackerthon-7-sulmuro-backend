package com.example.sulmuro_app.dto.translate.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TranslateRequest {

    @NotBlank
    private String sourceLanguageCode; // 번역할 언어 국가 코드 (e.g., "en")

    @NotBlank
    private String targetLanguageCode; // 번역 결과로 나올 언어 국가 코드 (e.g., "ko")

    @NotBlank
    private String text; // 번역할 문장
}