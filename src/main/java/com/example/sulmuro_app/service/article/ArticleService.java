package com.example.sulmuro_app.service.article;

import com.example.sulmuro_app.domain.article.Article;
import com.example.sulmuro_app.dto.article.request.ArticleCreateRequest;
import com.example.sulmuro_app.dto.article.response.ArticleListItemResponse;
import com.example.sulmuro_app.dto.article.response.ArticleResponse;
import com.example.sulmuro_app.domain.article.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public ArticleResponse createArticle(ArticleCreateRequest req) {
        Article a = new Article(
                req.getTitle(),
                req.getSubTitle(),
                req.getTheme(),
                null, // createdAt은 @PrePersist에서
                req.getLocation()
        );
        Article saved = articleRepository.save(a);
        return ArticleResponse.of(saved);
    }
    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        // 여러 개 id를 한 번에 조회
        List<Article> articles = articleRepository.findByArticleIdIn(ids);

        // ArticleResponse.of(...)는 네가 이미 쓰고 있으니까 재활용
        return articles.stream()
                .map(ArticleResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArticleResponse getArticle(Long articleId) {
        Article a = articleRepository.findByArticleId(articleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Article not found"));
        return ArticleResponse.of(a);
    }

    @Transactional(readOnly = true)
    public Page<ArticleListItemResponse> listArticles(Pageable pageable) {
        return articleRepository.findAllBy(pageable).map(ArticleListItemResponse::of);
    }
}
