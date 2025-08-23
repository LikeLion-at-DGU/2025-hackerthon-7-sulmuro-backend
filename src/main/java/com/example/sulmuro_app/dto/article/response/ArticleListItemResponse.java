package com.example.sulmuro_app.dto.article.response;

import com.example.sulmuro_app.domain.article.Article;
import com.example.sulmuro_app.domain.bin.Location;
import com.example.sulmuro_app.domain.bin.Theme;
import com.example.sulmuro_app.i18n.Trans;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleListItemResponse {

    private Long id;
    @Trans(src = "KO")
    private String title;
    @Trans(src = "KO")
    private String subTitle;
    private Theme theme;
    private LocalDateTime createdAt;
    private Location location;

    public static ArticleListItemResponse of(Article a) {
        ArticleListItemResponse r = new ArticleListItemResponse();
        r.id = a.getArticleId();
        r.title = a.getTitle();
        r.subTitle = a.getSubTitle();
        r.theme = a.getTheme();
        r.createdAt = a.getCreatedAt();
        r.location = a.getLocation();
        return r;
    }

    // getter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSubTitle() { return subTitle; }
    public Theme getTheme() { return theme; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Location getLocation() { return location; }
}
