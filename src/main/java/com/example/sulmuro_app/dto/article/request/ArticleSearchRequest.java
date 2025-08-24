package com.example.sulmuro_app.dto.article.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ArticleSearchRequest {
    @NotEmpty
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }
}
