package com.example.sulmuro_app.controller.chat;

import com.example.sulmuro_app.dto.chat.request.PostMessageRequest;
import com.example.sulmuro_app.dto.chat.response.ChatResponse;
import com.example.sulmuro_app.dto.chat.response.StartChatResponse;
import com.example.sulmuro_app.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@RestController
public class ChatController {

    private final ChatService chatService;
    @PostMapping("/start")
    public StartChatResponse startChatWithImage(
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        return chatService.startChat(imageFile);
    }

    @PostMapping("/{roomId}/message")
    public ChatResponse postMessage(
            @PathVariable Long roomId,
            @RequestBody PostMessageRequest request
    ) {
        return chatService.postMessage(roomId, request);
    }
}