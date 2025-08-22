package com.example.sulmuro_app.service.crawler;

import com.example.sulmuro_app.service.chat.GeminiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogContentProcessorService {

    private final GeminiService geminiService; // 기존 GeminiService를 재사용
    private final ObjectMapper objectMapper;

    public List<Map<String, Object>> processBlogContent(List<String> blogDescriptions) {
        if (blogDescriptions == null || blogDescriptions.isEmpty()) {
            return Collections.emptyList();
        }

        String combinedText = blogDescriptions.stream().collect(Collectors.joining("\n\n---\n\n"));
        String extractedJson = geminiService.extractMarketInfoFromText(combinedText);

        try {
            // Gemini가 반환한 JSON 문자열을 Java 객체로 변환
            return objectMapper.readValue(extractedJson, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}