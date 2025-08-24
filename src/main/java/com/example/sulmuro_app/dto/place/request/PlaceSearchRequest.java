package com.example.sulmuro_app.dto.place.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class PlaceSearchRequest {
    @NotEmpty List<Long> ids;

    public List<Long> getIds(){
        return ids;
}

}
