package com.example.sulmuro_app.dto.article.block.response;

import com.example.sulmuro_app.domain.article.ArticleBlock;
import com.example.sulmuro_app.domain.bin.BlockType;

import java.time.LocalDateTime;

public class BlockResponse {

    private Long id;
    private BlockType type;
    private String data;
    private Long position;
    private LocalDateTime createdAt;

    public static BlockResponse of(ArticleBlock b) {
        BlockResponse r = new BlockResponse();
        r.id = b.getBlockId();
        r.type = b.getType();
        r.data = b.getData();
        r.position = b.getPosition();
        r.createdAt = b.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public BlockType getType() { return type; }
    public String getData() { return data; }
    public Long getPosition() { return position; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
