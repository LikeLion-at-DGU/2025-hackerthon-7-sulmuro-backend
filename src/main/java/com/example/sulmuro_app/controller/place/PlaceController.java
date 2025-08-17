package com.example.sulmuro_app.controller.place;

import com.example.sulmuro_app.domain.place.Place;
import com.example.sulmuro_app.domain.place.PlaceRepository;
import com.example.sulmuro_app.dto.bin.ApiResponse;
import com.example.sulmuro_app.dto.place.request.PlaceCreateRequest;
import com.example.sulmuro_app.dto.place.response.PlaceDetailResponse;
import com.example.sulmuro_app.dto.place.response.PlaceListResponse;
import com.example.sulmuro_app.service.place.PlaceService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceRepository placeRepository;


    @PostMapping
    public void saveRequest(@RequestBody PlaceCreateRequest request) {placeService.savePlace(request);}

    @GetMapping
    public ApiResponse<List<PlaceListResponse>> getAllPlaces() {
        List<PlaceListResponse> places = placeService.findList();
        return ApiResponse.success("전체 장소 목록 조회 성공", places);
    }

    @GetMapping("/{id}")
    public ApiResponse<PlaceDetailResponse> getPlaceById(@PathVariable Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place not found with id " + id));
        return ApiResponse.success("장소 상세 조회 성공", new PlaceDetailResponse(place));
    }
    }

