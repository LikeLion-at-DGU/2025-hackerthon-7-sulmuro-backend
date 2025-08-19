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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final MarketInfoService marketInfoService; // MarketInfoService 주입
    private static final Logger log = LoggerFactory.getLogger(ChatService.class); // Logger 객체 추가

    @Transactional
    public StartChatResponse startChat(MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어있습니다.");
        }

        // 1. 1차 이미지 분석으로 itemName만 추출
        String itemName = geminiService.extractItemNameFromImage(imageFile);

        // 2. 추출된 itemName으로 DB에서 관련 가게 정보 조회
        String marketInfo = marketInfoService.findMarketInfoByItemName(itemName);

        // 3. 이미지와 DB 정보를 모두 활용하여 최종 답변 생성
        String finalResponse = geminiService.askToGeminiWithImageAndContext(imageFile, marketInfo);

        // Gemini 응답 가공 및 저장
        String cleanedJsonString = cleanGeminiResponse(finalResponse);

        // ⭐️⭐️⭐️ [해결 방법] 어떤 응답이 왔는지 여기서 로그로 확인! ⭐️⭐️⭐️
        log.info("Gemini 최종 응답 (파싱 전): {}", cleanedJsonString);

        JsonNode jsonNode = parseJson(cleanedJsonString);
        String topic = jsonNode.get("itemName").asText();

        ChatRoom newChatRoom = chatRoomRepository.save(new ChatRoom(topic));
        chatMessageRepository.save(new ChatMessage(newChatRoom, "ai", cleanedJsonString));

        return new StartChatResponse(newChatRoom.getId(), jsonNode);
    }
    @Transactional
    public ChatResponse postMessage(Long roomId, PostMessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        chatMessageRepository.save(new ChatMessage(chatRoom, "user", request.getMessage()));

        // DB에서 채팅 주제와 관련된 가게 정보를 조회
        String marketInfo = marketInfoService.findMarketInfoByItemName(chatRoom.getTopic());

        // 조회된 정보와 함께 Gemini에 질문
        String aiResponse = geminiService.askToGeminiWithMessage(chatRoom.getTopic(), request.getMessage(), marketInfo);

        chatMessageRepository.save(new ChatMessage(chatRoom, "ai", aiResponse));

        return new ChatResponse(aiResponse);
    }

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

    private JsonNode parseJson(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱에 실패했습니다.", e);
        }
    }
}