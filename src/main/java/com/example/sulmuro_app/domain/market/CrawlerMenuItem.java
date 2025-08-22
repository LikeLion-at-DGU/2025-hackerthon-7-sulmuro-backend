package com.example.sulmuro_app.domain.market;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "crawler_menu_item") // 실제 테이블 이름 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlerMenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private CrawlerStore store;

    @Column(nullable = false)
    private String name;

    private Integer price;

    public CrawlerMenuItem(CrawlerStore store, String name, Integer price) {
        this.store = store;
        this.name = name;
        this.price = price;
    }
}