package com.example.sulmuro_app.dto.article.request;

import com.example.sulmuro_app.domain.bin.BlockType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BlockCreateRequest {

    @NotNull
    private BlockType type;

    @NotBlank
    private String data; // 이미지 URL 또는 텍스트

    @NotNull
    private Long position;

    public BlockType getType() { return type; }
    public String getData() { return data; }
    public Long getPosition() { return position; }

    public void setType(BlockType type) { this.type = type; }
    public void setData(String data) { this.data = data; }
    public void setPosition(Long position) { this.position = position; }
}
