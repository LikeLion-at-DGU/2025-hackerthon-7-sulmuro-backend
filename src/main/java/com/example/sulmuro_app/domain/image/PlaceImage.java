package com.example.sulmuro_app.domain.image;

import com.example.sulmuro_app.domain.place.Place;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PlaceImage")
public class PlaceImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")         // PK
    private Long imageId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false, length = 1024)
    private String url;

    @Column(length = 255)
    private String filename;

    @Column(nullable = false)
    private Boolean isCover = false;

    @Column(nullable = false,name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getImageId() {
        return imageId;
    }

    public Place getPlace() {
        return place;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    public Boolean getCover() {
        return isCover;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setCover(Boolean cover) {
        isCover = cover;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
