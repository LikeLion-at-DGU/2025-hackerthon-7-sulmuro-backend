package com.example.sulmuro_app.controller.place;

import com.example.sulmuro_app.dto.place.request.PlaceCreateRequest;
import com.example.sulmuro_app.service.place.PlaceService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {this.placeService = placeService;}

    @PostMapping("api/v1/places")
    public void saveRequest(@RequestBody PlaceCreateRequest request) {placeService.savePlace(request);}

    }

