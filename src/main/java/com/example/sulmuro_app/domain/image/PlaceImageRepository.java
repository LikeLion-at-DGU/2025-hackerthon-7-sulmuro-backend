package com.example.sulmuro_app.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {
    List<PlaceImage> findByPlace_PlaceIdOrderByCreatedAtDesc(Long placeId);

}
