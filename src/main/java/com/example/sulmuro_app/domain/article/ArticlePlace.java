package com.example.sulmuro_app.domain.article;

import com.example.sulmuro_app.domain.place.Place;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

// ArticlePlace.java
@Entity
@Table(name = "article_place",
        uniqueConstraints = @UniqueConstraint(columnNames = {"article_id", "place_id"}))
@Getter
@NoArgsConstructor
public class ArticlePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "ord")
    private Integer ord;

    public ArticlePlace(Article article, Place place, Integer ord) {
        this.article = article;
        this.place = place;
        this.ord = ord;
    }
}

