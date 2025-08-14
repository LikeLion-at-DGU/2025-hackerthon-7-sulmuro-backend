package com.example.sulmuro_app.controller.chat;

import com.example.sulmuro_app.dto.chat.response.ChatResponse;
import com.example.sulmuro_app.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/api/chat")
@RestController
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/ask")
    public ChatResponse askToAi(
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        return chatService.getAiResponse(imageFile);
    }
}