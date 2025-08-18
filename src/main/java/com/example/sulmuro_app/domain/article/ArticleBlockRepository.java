package com.example.sulmuro_app.repository.article;

import com.example.sulmuro_app.domain.article.ArticleBlock;
import com.example.sulmuro_app.domain.bin.BlockType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleBlockRepository extends JpaRepository<ArticleBlock, Long> {

    List<ArticleBlock> findByArticle_ArticleIdOrderByPositionAscBlockIdAsc(Long articleId);

    List<ArticleBlock> findTop5ByArticle_ArticleIdAndTypeOrderByPositionAscBlockIdAsc(
            Long articleId, BlockType type
    );
}
