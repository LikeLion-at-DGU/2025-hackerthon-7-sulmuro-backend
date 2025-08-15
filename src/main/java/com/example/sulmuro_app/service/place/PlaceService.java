package com.example.sulmuro_app.service.place;

import com.example.sulmuro_app.domain.place.Place;
import com.example.sulmuro_app.domain.place.PlaceRepository;
import com.example.sulmuro_app.dto.place.request.PlaceCreateRequest;
import com.example.sulmuro_app.dto.place.response.PlaceDetailResponse;
import com.example.sulmuro_app.dto.place.response.PlaceListResponse;
import com.example.sulmuro_app.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    @Transactional
    public void savePlace(PlaceCreateRequest request){
        placeRepository.save(new Place(request.getName(),request.getContent(),request.getCategory(),request.getLat(),request.getLng(),request.getAddress(),request.getLocation()));
    }

    @Transactional(readOnly = true)
    public List<PlaceListResponse> findList(){
        return placeRepository.findAll().stream().map(PlaceListResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlaceDetailResponse findDetail(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다. id=" + id));

        return new PlaceDetailResponse(place); // images는 null 그대로
    }



}

