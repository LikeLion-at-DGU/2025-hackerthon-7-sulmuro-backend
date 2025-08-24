package com.example.sulmuro_app.controller.place;

import com.example.sulmuro_app.domain.place.Place;
import com.example.sulmuro_app.domain.place.PlaceRepository;
import com.example.sulmuro_app.dto.bin.ApiResponse;
import com.example.sulmuro_app.dto.place.request.PlaceCreateRequest;
import com.example.sulmuro_app.dto.place.request.PlaceSearchRequest;
import com.example.sulmuro_app.dto.place.response.PlaceDetailResponse;
import com.example.sulmuro_app.dto.place.response.PlaceListResponse;
import com.example.sulmuro_app.dto.place.response.PlaceSearchResponse;
import com.example.sulmuro_app.i18n.TranslateResponse;
import com.example.sulmuro_app.service.place.PlaceSearchService;
import com.example.sulmuro_app.service.place.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceRepository placeRepository;
    private final PlaceSearchService placeSearchService;


    @PostMapping
    public void saveRequest(@RequestBody PlaceCreateRequest request) {placeService.savePlace(request);}


    @PostMapping("/search")
    @TranslateResponse
    public ApiResponse<List<PlaceSearchResponse>> searchPlace(@RequestBody PlaceSearchRequest req) {
        List<PlaceSearchResponse> places = placeSearchService.searchByIds(req.getIds());
        return ApiResponse.success("장소 목록을 검색하여 불러왔습니다.",places);
    }
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

