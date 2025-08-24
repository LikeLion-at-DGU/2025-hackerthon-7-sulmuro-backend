package com.example.sulmuro_app.domain.article;

import com.example.sulmuro_app.domain.bin.BlockType;
import com.example.sulmuro_app.domain.bin.Location;
import com.example.sulmuro_app.domain.bin.Theme;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId;

    @Column
    private String title;

    @Column
    private String subTitle;


    @Enumerated(EnumType.STRING)
    @Column
    private Theme theme;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column
    private Location location;


    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC, blockId ASC")
    private List<ArticleBlock> blocks = new ArrayList<>();

    // 조회 전용(직렬화/응답용) 리스트 — DB 컬럼 아님
    @Transient
    private List<String> imageUrls;

    protected Article() {} // JPA 기본 생성자

    public Article(String title, String subTitle, Theme theme, LocalDateTime createdAt, Location location) {
        this.title = title;
        this.subTitle = subTitle;
        this.theme = theme;
        this.createdAt = createdAt;
        this.location = location;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void addBlock(ArticleBlock block) {
        if (block == null) return;
        this.blocks.add(block);
        block.setArticle(this); // 양방향 일관성 유지
    }

    public void removeBlock(ArticleBlock block) {
        if (block == null) return;
        this.blocks.remove(block);
        block.setArticle(null);
    }

    public List<String> getImageUrls() {
        if (blocks == null) return List.of();
        return blocks.stream()
                .filter(b -> b.getType() == BlockType.IMAGE)
                .map(ArticleBlock::getData)
                .filter(Objects::nonNull)
                .limit(5)
                .toList();
    }

    public Long getArticleId() { return articleId; }
    public String getTitle() { return title; }
    public String getSubTitle() { return subTitle; }
    public Theme getTheme() { return theme; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Location getLocation() { return location; }
    public List<ArticleBlock> getBlocks() { return blocks; }

    public void setTitle(String title) { this.title = title; }
    public void setSubTitle(String subTitle) { this.subTitle = subTitle; }
    public void setTheme(Theme theme) { this.theme = theme; }
    public void setLocation(Location location) { this.location = location; }
}
