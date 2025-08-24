package com.example.sulmuro_app.dto.place.response;

import com.example.sulmuro_app.domain.bin.Location;
import com.example.sulmuro_app.domain.bin.PlaceCategory;

import java.math.BigDecimal;
import java.util.List;

public class PlaceSearchResponse {

    private Long id;

    private String name;

    private PlaceCategory category;

    private String content;

    private BigDecimal lat;
    private BigDecimal lng;

    private String address;

    private Location location;

    private List<String> image; // 여러 개 URL

    protected PlaceSearchResponse() {
    }

    public PlaceSearchResponse(Long id, String name, PlaceCategory category, String content,
                               BigDecimal lat, BigDecimal lng, String address,
                               Location location, List<String> image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.content = content;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.location = location;
        this.image = image;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PlaceCategory getCategory() {
        return category;
    }

    public String getContent() {
        return content;
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

    public List<String> getImage() {
        return image;
    }
}
