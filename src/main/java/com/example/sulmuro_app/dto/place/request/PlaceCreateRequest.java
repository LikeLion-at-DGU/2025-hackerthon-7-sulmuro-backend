package com.example.sulmuro_app.dto.place.request;

import com.example.sulmuro_app.domain.bin.Location;
import com.example.sulmuro_app.domain.bin.PlaceCategory;

import java.math.BigDecimal;

public class PlaceCreateRequest {

    private String name;
    private String content;
    private PlaceCategory category;
    private BigDecimal lat;
    private BigDecimal lng;
    private String address;
    private Location location;

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public PlaceCategory getCategory() {
        return category;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }

    public Location getLocation() {
        return location;
    }
}
