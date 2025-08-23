package com.example.sulmuro_app.dto.translate.response;

import lombok.Getter;
import java.util.List;

@Getter
public class TranslateRecommendResponse {
    private final String translatedText;
    // 타입을 RecommendationPair의 리스트로 변경
    private final List<RecommendationPair> recommendations;

    public TranslateRecommendResponse(String translatedText, List<RecommendationPair> recommendations) {
        this.translatedText = translatedText;
        this.recommendations = recommendations;
    }
}