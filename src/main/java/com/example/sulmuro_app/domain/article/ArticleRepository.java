package com.example.sulmuro_app.repository.article;

import com.example.sulmuro_app.domain.article.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @EntityGraph(attributePaths = "blocks")
    Optional<Article> findByArticleId(Long articleId);

    @EntityGraph(attributePaths = "blocks")
    Page<Article> findAllBy(Pageable pageable);
}
