package com.example.sulmuro_app.dto.place.response;

import com.example.sulmuro_app.domain.bin.Location;
import com.example.sulmuro_app.domain.bin.PlaceCategory;

import java.math.BigDecimal;
import java.util.List;

public record PlaceSearchResponse(
        Long id,
        String name,
        PlaceCategory category, // ← 그대로 두면 JSON에는 "Food", "Clothes" 내려감
        String content,
        BigDecimal lat,
        BigDecimal lng,
        String address,
        Location location,
        List<String> image // 여러 개 URL
) {}
