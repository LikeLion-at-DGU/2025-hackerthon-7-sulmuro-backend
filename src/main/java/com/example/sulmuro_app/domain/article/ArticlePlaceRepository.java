package com.example.sulmuro_app.domain.article;

import com.example.sulmuro_app.domain.place.Place;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticlePlaceRepository extends JpaRepository<ArticlePlace, Long> {

    @Query("""
        select p
        from ArticlePlace ap
        join ap.place p
        where ap.article.articleId = :articleId
        order by coalesce(ap.ord, 999999), p.placeId
    """)
    List<Place> findPlacesByArticleId(@Param("articleId") Long articleId);
}
