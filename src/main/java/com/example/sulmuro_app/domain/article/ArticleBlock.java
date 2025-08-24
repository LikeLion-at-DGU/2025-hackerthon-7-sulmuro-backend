package com.example.sulmuro_app.domain.article;

import com.example.sulmuro_app.domain.bin.BlockType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ArticleBlock {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    private Long blockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(nullable = false,name = "created_at")
    private LocalDateTime createdAt;

    @Column(length = 1023)
    private String data;

    @Column
    private Long position;

    @Column
    @Enumerated(EnumType.STRING)
    private BlockType type;

    protected ArticleBlock() {}

    public ArticleBlock(Article article, String data, Long position, BlockType type) {
        this.article = article;
        this.data = data;
        this.position = position;
        this.type = type;
    }
    @PrePersist
    private void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public Long getBlockId() { return blockId; }
    public Article getArticle() { return article; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getData() { return data; }
    public Long getPosition() { return position; }
    public BlockType getType() { return type; }

    public void setArticle(Article article) { this.article = article; }
    public void setData(String data) { this.data = data; }
    public void setPosition(Long position) { this.position = position; }
    public void setType(BlockType type) { this.type = type; }

}
