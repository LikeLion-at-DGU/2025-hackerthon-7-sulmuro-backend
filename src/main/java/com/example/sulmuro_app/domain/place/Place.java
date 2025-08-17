package com.example.sulmuro_app.domain.place;

import com.example.sulmuro_app.domain.place.bin.Location;
import com.example.sulmuro_app.domain.place.bin.PlaceCategory;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "Place")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="place_id")
    private Long placeId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceCategory category;

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal lat;

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal lng;

    @Column(length = 255)
    private String address;

    @Column
    private Long image_id; // FK, 관계 매핑은 나중에 가능

    @Column(nullable = false,name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Location location = Location.GWANGJANG_MARKET;

    // ===== 기본 생성자 =====
    protected Place() {}

    public Place(String name, String content, PlaceCategory category, BigDecimal lat, BigDecimal lng,String address, Location location) {
        this.name = Objects.requireNonNull(name, "name is required");
        this.content = content;
        this.category = Objects.requireNonNull(category, "category is required");
        this.lat = Objects.requireNonNull(lat, "lat is required");
        this.lng = Objects.requireNonNull(lng, "lng is required");
        this.location = Objects.requireNonNull(location, "location is required");
        this.address = address;


    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====


    public Long getPlace_id() {
        return placeId;
    }

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

    public Long getImage_id() {
        return image_id;
    }

    public LocalDateTime getCreated_at() {
        return createdAt;
    }

    public Location getLocation() {
        return location;
    }

    public void setImage_id(Long image_id) {
        this.image_id = image_id;
    }
}
