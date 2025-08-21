package com.example.sulmuro_app.service.place;

import com.example.sulmuro_app.domain.image.PlaceImageRepository;
import com.example.sulmuro_app.domain.place.Place;
import com.example.sulmuro_app.domain.place.PlaceRepository;
import com.example.sulmuro_app.dto.image.place.response.PlaceImageResponse;
import com.example.sulmuro_app.dto.place.request.PlaceCreateRequest;
import com.example.sulmuro_app.dto.place.response.PlaceDetailResponse;
import com.example.sulmuro_app.dto.place.response.PlaceListResponse;
import com.example.sulmuro_app.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;
    @Transactional
    public void savePlace(PlaceCreateRequest request){
        placeRepository.save(new Place(request.getName(),request.getContent(),request.getCategory(),request.getLat(),request.getLng(),request.getAddress(),request.getLocation()));
    }

    @Transactional(readOnly = true)
    public List<PlaceListResponse> findList() {
        // 정렬은 필요에 맞게
        List<Place> places = placeRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt"));

        // 1) cover image_id 수집
        Set<Long> coverIds = places.stream()
                .map(Place::getImage_id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 2) 한 번에 조회해서 id→url 맵
        Map<Long, String> urlById;
        if (!coverIds.isEmpty()) {
            var images = placeImageRepository.findAllById(coverIds);
            urlById = new java.util.HashMap<>();
            for (var img : images) {
                urlById.put(img.getImageId(), img.getUrl());
            }
        } else {
            urlById = Collections.emptyMap();
        }

        // 3) DTO 매핑
        return places.stream()
                .map(p -> new PlaceListResponse(p, urlById.get(p.getImage_id())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlaceDetailResponse findDetail(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다. id=" + id));

        return new PlaceDetailResponse(place); // images는 null 그대로
    }



}

