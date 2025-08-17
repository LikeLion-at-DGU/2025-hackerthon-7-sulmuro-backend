package com.example.sulmuro_app.dto.place.response;

import com.example.sulmuro_app.domain.place.Place;
import com.example.sulmuro_app.domain.place.bin.Location;
import com.example.sulmuro_app.domain.place.bin.PlaceCategory;
import com.example.sulmuro_app.dto.image.place.response.PlaceImageResponse;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
@Getter
public class PlaceDetailResponse {
    private Long id;
    private String name;
    private PlaceCategory category;
    private String content;
    private String address;
    private Location location;
    private BigDecimal lat;
    private BigDecimal lng;


    public PlaceDetailResponse(Place p) {
        this.id = p.getPlace_id();
        this.name = p.getName();
        this.category = p.getCategory();
        this.content = p.getContent();
        this.address = p.getAddress();
        this.location = p.getLocation();
        this.lat = p.getLat();
        this.lng = p.getLng();
    }
}
