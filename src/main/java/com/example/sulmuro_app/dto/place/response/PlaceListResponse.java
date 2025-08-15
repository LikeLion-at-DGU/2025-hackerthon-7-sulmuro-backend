package com.example.sulmuro_app.dto.place.response;

import com.example.sulmuro_app.domain.place.Place;
import com.example.sulmuro_app.domain.place.bin.PlaceCategory;
import lombok.Getter;

import java.math.BigDecimal;
@Getter
public class PlaceListResponse {
    private Long id;
    private String name;
    private PlaceCategory category;
    private BigDecimal lat;
    private BigDecimal lng;

    private String cover_image;

    public PlaceListResponse(Place p) {
        this.id = p.getPlace_id();
        this.name = p.getName();
        this.category = p.getCategory();
        this.lat = p.getLat();
        this.lng = p.getLng();
        this.cover_image = null;
    }


}
