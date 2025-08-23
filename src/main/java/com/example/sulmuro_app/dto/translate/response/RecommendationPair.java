package com.example.sulmuro_app.dto.translate.response;

import lombok.Getter;
import lombok.Setter;

// Lombok 어노테이션 추가
@Getter
@Setter
public class RecommendationPair {
    private String source;
    private String target;
}
