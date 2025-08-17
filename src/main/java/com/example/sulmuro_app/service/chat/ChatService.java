package com.example.sulmuro_app.service.chat;

import com.example.sulmuro_app.domain.chat.ChatMessage;
import com.example.sulmuro_app.domain.chat.ChatRoom;
import com.example.sulmuro_app.dto.chat.request.PostMessageRequest;
import com.example.sulmuro_app.dto.chat.response.ChatResponse;
import com.example.sulmuro_app.dto.chat.response.StartChatResponse;
import com.example.sulmuro_app.repository.chat.ChatMessageRepository;
import com.example.sulmuro_app.repository.chat.ChatRoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;
    private final GeminiService geminiService;


    @Transactional
    public StartChatResponse startChat(MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어있습니다.");
        }

        // Gemini에 첫 질문
        String rawResponse = geminiService.askToGeminiWithImage(imageFile);

        // Gemini 응답 가공 (Markdown 코드 블록 제거)
        String cleanedJsonString = cleanGeminiResponse(rawResponse);

        // 주제(itemName) 추출
        JsonNode jsonNode = parseJson(cleanedJsonString);
        String topic = jsonNode.get("itemName").asText();

        // DB에 채팅방 및 첫 메시지 저장
        ChatRoom newChatRoom = chatRoomRepository.save(new ChatRoom(topic));
        chatMessageRepository.save(new ChatMessage(newChatRoom, "ai", cleanedJsonString));

        // 최종 응답 객체 생성
        return new StartChatResponse(newChatRoom.getId(), jsonNode);
    }

    @Transactional
    public ChatResponse postMessage(Long roomId, PostMessageRequest request) {
        // 채팅방 정보 가져오기
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        // 사용자 메시지 저장
        chatMessageRepository.save(new ChatMessage(chatRoom, "user", request.getMessage()));


        // Gemini에 후속 질문 (GeminiService에 위임)
        String aiResponse = geminiService.askToGeminiWithMessage(chatRoom.getTopic(), request.getMessage());

        // AI 응답 저장
        chatMessageRepository.save(new ChatMessage(chatRoom, "ai", aiResponse));

        return new ChatResponse(aiResponse);
    }
    /**
     * Gemini 응답에서 Markdown 코드 블록을 제거합니다.
     */
    private String cleanGeminiResponse(String rawResponse) {
        String cleaned = rawResponse.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    /**
     * JSON 문자열을 JsonNode 객체로 파싱합니다.
     */
    private JsonNode parseJson(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            // 실제 프로덕션 코드에서는 로깅을 하고, 커스텀 예외를 던지는 것이 좋습니다.
            throw new RuntimeException("JSON 파싱에 실패했습니다.", e);
        }
    }
}