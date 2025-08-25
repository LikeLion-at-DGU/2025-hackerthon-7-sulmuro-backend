package com.example.sulmuro_app.service.article;

import com.example.sulmuro_app.domain.article.ArticleRepository;
import com.example.sulmuro_app.domain.article.ArticlePlaceRepository;
import com.example.sulmuro_app.domain.image.PlaceImage;
import com.example.sulmuro_app.domain.image.PlaceImageRepository;
import com.example.sulmuro_app.domain.place.Place;
import com.example.sulmuro_app.dto.place.response.PlaceSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ArticlePlaceService {

    private final ArticleRepository articleRepository;
    private final ArticlePlaceRepository articlePlaceRepository;
    private final PlaceImageRepository placeImageRepository;

    @Transactional(readOnly = true)
    public List<PlaceSearchResponse> getRecommendedPlaces(Long articleId) {
        // 1) 아티클 존재 확인 (404)
        articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        // 2) 아티클에 연결된 장소들(노출 순서 포함)
        List<Place> places = articlePlaceRepository.findPlacesByArticleId(articleId);
        if (places.isEmpty()) return List.of();

        // 3) 모든 placeId의 이미지를 한 번에 조회(커버 우선, 오래된 순)
        List<Long> placeIds = places.stream().map(Place::getPlace_id).toList();
        List<PlaceImage> images = placeImageRepository.findByPlace_PlaceIdInOrderByIsCoverDescCreatedAtAsc(placeIds);

        // 4) placeId -> url 리스트로 그룹핑
        Map<Long, List<String>> urlsByPlace = new LinkedHashMap<>();
        for (PlaceImage pi : images) {
            Long pid = pi.getPlace().getPlace_id();
            urlsByPlace.computeIfAbsent(pid, k -> new ArrayList<>()).add(pi.getUrl());
        }

        // (옵션) 장소당 최대 N장 제한
        int maxPerPlace = 6; // 샘플 응답처럼 6장 정도
        urlsByPlace.replaceAll((k, v) -> v.size() > maxPerPlace ? v.subList(0, maxPerPlace) : v);

        // 5) DTO 매핑 (요청한 포맷 그대로)
        List<PlaceSearchResponse> out = new ArrayList<>(places.size());
        for (Place p : places) {
            out.add(new PlaceSearchResponse(
                    p.getPlace_id(),
                    p.getName(),
                    p.getCategory(),
                    p.getContent(),
                    p.getLat(),
                    p.getLng(),
                    p.getAddress(),
                    p.getLocation(),
                    urlsByPlace.getOrDefault(p.getPlace_id(), List.of())
            ));
        }
        return out;
    }
}
