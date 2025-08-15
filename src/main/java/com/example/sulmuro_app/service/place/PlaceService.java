package com.example.sulmuro_app.service.place;

import com.example.sulmuro_app.domain.place.Place;
import com.example.sulmuro_app.domain.place.PlaceRepository;
import com.example.sulmuro_app.dto.place.request.PlaceCreateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    @Transactional
    public void savePlace(PlaceCreateRequest request){
        placeRepository.save(new Place(request.getName(),request.getContent(),request.getCategory(),request.getLat(),request.getLng(),request.getAddress(),request.getLocation()));
    }

}

