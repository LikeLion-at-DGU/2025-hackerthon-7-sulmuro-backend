package com.example.sulmuro_app.dto.article.response;

import com.example.sulmuro_app.domain.article.Article;
import com.example.sulmuro_app.domain.bin.Location;
import com.example.sulmuro_app.domain.bin.Theme;
import com.example.sulmuro_app.i18n.Trans;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleResponse {

    private Long id;
    @Trans(src="KO")
    private String title;
    @Trans(src="KO")
    private String subTitle;
    private Theme theme;
    private LocalDateTime createdAt;
    private Location location;
    private List<String> imageUrls; // type=IMAGE 중 최대 5개

    public static ArticleResponse of(Article a) {
        ArticleResponse r = new ArticleResponse();
        r.id = a.getArticleId();
        r.title = a.getTitle();
        r.subTitle = a.getSubTitle();
        r.theme = a.getTheme();
        r.createdAt = a.getCreatedAt();
        r.location = a.getLocation();
        r.imageUrls = a.getImageUrls(); // 엔티티의 조회용 메서드 사용
        return r;
    }

    // getter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSubTitle() { return subTitle; }
    public Theme getTheme() { return theme; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Location getLocation() { return location; }
    public List<String> getImageUrls() { return imageUrls; }
}
