package com.example.sulmuro_app.dto.chat.response;

import lombok.Getter;

@Getter
public class StartChatResponse {
    private final Long roomId;
    private final String answer;

    public StartChatResponse(Long roomId, String answer) {
        this.roomId = roomId;
        this.answer = answer;
    }
}