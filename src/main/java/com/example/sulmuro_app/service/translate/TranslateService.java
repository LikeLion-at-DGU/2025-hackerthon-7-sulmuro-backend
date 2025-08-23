package com.example.sulmuro_app.service.translate;

import com.example.sulmuro_app.dto.translate.request.TranslateRequest;
import com.example.sulmuro_app.dto.translate.response.RecommendationPair;
import com.example.sulmuro_app.dto.translate.response.TranslateRecommendResponse;
import com.example.sulmuro_app.dto.translate.response.TranslateResponse;
import com.example.sulmuro_app.service.chat.GeminiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranslateService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    // 1. 단순 번역 API
    public TranslateResponse translate(TranslateRequest request) {
        String translatedText = geminiService.translateText(
                request.getSourceLanguageCode(),
                request.getTargetLanguageCode(),
                request.getText()
        );
        return new TranslateResponse(translatedText);
    }

    // 2. 번역 + 추천 표현 API

    // [수정된 부분]
    public TranslateRecommendResponse translateWithRecommend(TranslateRequest request) {
        String jsonResponse = geminiService.translateTextWithRecommendations(
                request.getSourceLanguageCode(),
                request.getTargetLanguageCode(),
                request.getText()
        );

        try {
            // objectMapper가 JSON을 Map<String, Object> 형태로 변환
            Map<String, Object> resultMap = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
            String translatedText = (String) resultMap.getOrDefault("translatedText", "");

            // recommendations는 List<Map<String, String>> 형태이므로, 이를 List<RecommendationPair>로 변환
            List<Map<String, String>> rawRecommendations = (List<Map<String, String>>) resultMap.getOrDefault("recommendations", Collections.emptyList());

            List<RecommendationPair> recommendations = objectMapper.convertValue(rawRecommendations, new TypeReference<List<RecommendationPair>>() {});

            return new TranslateRecommendResponse(translatedText, recommendations);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini API response.", e);
        }
    }
}