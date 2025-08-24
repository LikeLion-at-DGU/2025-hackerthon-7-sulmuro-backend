package com.example.sulmuro_app.domain.market;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
@Getter
@Entity
@Table(name = "crawler_store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlerStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String notes;

    private String locationDesc;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<CrawlerMenuItem> menuItems = new ArrayList<>();

    public CrawlerStore(String name, String notes, String locationDesc) {
        this.name = name;
        this.notes = notes;
        this.locationDesc = locationDesc;
    }
}