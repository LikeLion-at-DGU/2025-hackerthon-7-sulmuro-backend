package com.example.sulmuro_app.dto.chat.response;

import lombok.Getter;

@Getter
public class StartChatResponse {
    private final Long roomId;
    private final Object answer;

    public StartChatResponse(Long roomId, Object answer) {
        this.roomId = roomId;
        this.answer = answer;
    }
}