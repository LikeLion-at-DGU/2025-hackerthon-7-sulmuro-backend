package com.example.sulmuro_app.domain.image;

import com.example.sulmuro_app.domain.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {
    List<PlaceImage> findByPlace_PlaceIdOrderByCreatedAtDesc(Long placeId);
    List<PlaceImage> findByPlaceIn(Collection<Place> places);
    List<PlaceImage> findByPlace_PlaceIdInOrderByIsCoverDescCreatedAtAsc(Collection<Long> placeIds);

}
