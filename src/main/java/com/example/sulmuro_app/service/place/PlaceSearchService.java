package com.example.sulmuro_app.service.place;

import com.example.sulmuro_app.domain.place.Place;
import com.example.sulmuro_app.domain.image.PlaceImage;
import com.example.sulmuro_app.dto.place.response.PlaceSearchResponse;
import com.example.sulmuro_app.domain.image.PlaceImageRepository;
import com.example.sulmuro_app.domain.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceSearchService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;

    @Transactional(readOnly = true)
    public List<PlaceSearchResponse> searchByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        // 1) 장소 일괄 조회
        List<Place> places = placeRepository.findByPlaceIdIn(ids);
        if (places.isEmpty()) return List.of();

        // 요청 순서 보존
        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);

        // 2) 이미지 일괄 조회 (엔티티 컬렉션 기반)
        List<PlaceImage> images = placeImageRepository.findByPlaceIn(places);

        // 정렬 규칙:
        //  - isCover = true 먼저
        //  - createdAt 오름차순
        //  - imageId 오름차순 (tie-breaker)
        images.sort(
                Comparator
                        .comparing(PlaceImage::getCover, Comparator.nullsLast(Boolean::compareTo)).reversed()
                        .thenComparing(PlaceImage::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(PlaceImage::getImageId, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        // 3) placeId → 이미지 URL 리스트로 그룹핑
        Map<Long, List<String>> urlsByPlaceId = images.stream()
                .collect(Collectors.groupingBy(
                        pi -> pi.getPlace().getPlace_id(),     // 프록시여도 ID 접근은 지연로딩 없이 가능
                        Collectors.mapping(PlaceImage::getUrl, Collectors.toList())
                ));

        // 4) DTO 매핑 (요청 순서대로)
        return places.stream()
                .sorted(Comparator.comparingInt(p -> order.getOrDefault(p.getPlace_id(), Integer.MAX_VALUE)))
                .map(p -> new PlaceSearchResponse(
                        p.getPlace_id(),
                        p.getName(),
                        // category 표기: Enum에 display()가 있으면 그것 사용, 없으면 name()으로 대응
                        p.getCategory(),
                        p.getContent(),
                        p.getLat(),
                        p.getLng(),
                        p.getAddress(),
                        p.getLocation(),
                        urlsByPlaceId.getOrDefault(p.getPlace_id(), List.of())
                ))
                .collect(Collectors.toList());
    }

}
