package com.example.sulmuro_app.controller.article;

import com.example.sulmuro_app.dto.article.request.ArticleCreateRequest;
import com.example.sulmuro_app.dto.article.response.ArticleListItemResponse;
import com.example.sulmuro_app.dto.article.response.ArticleResponse;
import com.example.sulmuro_app.dto.bin.ApiResponse;
import com.example.sulmuro_app.service.article.ArticleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public ApiResponse<ArticleResponse> create(@Valid @RequestBody ArticleCreateRequest req) {
        ArticleResponse res = articleService.createArticle(req);
        return ApiResponse.success("아티클이 생성되었습니다.", res);
    }

    @GetMapping("/{articleId}")
    public ApiResponse<ArticleResponse> getOne(@PathVariable Long articleId) {
        ArticleResponse res = articleService.getArticle(articleId);
        return  ApiResponse.success("아티클을 불러왔습니다.",  res);
    }

    /** 아티클 목록 조회 (페이지네이션) */
    @GetMapping
    public ApiResponse<Page<ArticleListItemResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ArticleListItemResponse> res = articleService.listArticles(PageRequest.of(page, size));
        return ApiResponse.success("아티클 목록을 불러왔습니다.", res);
    }
}
