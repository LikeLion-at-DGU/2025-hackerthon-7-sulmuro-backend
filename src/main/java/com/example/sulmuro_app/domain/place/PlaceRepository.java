package com.example.sulmuro_app.domain.place;

import com.example.sulmuro_app.dto.place.response.PlaceDetailResponse;
import com.example.sulmuro_app.dto.place.response.PlaceListResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {


}
