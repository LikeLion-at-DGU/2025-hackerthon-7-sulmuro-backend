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
    private final DBInfoService dbInfoService;
    private static final Logger log = LoggerFactory.getLogger(ChatService.class); 

    @Transactional
    public StartChatResponse startChat(MultipartFile imageFile,String language) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어있습니다.");
        }


        // 2. itemName으로 특정 가게만 찾는 대신, 전체 가게 정보를 컨텍스트로 제공
        String marketInfo = dbInfoService.getAllMarketInfo();

        // 3. 이미지와 DB 정보를 모두 활용하여 최종 답변 생성
        String finalResponse = geminiService.askToGeminiWithImageAndContext(imageFile, marketInfo,language);

        // Gemini 응답 가공 및 저장
        String cleanedJsonString = cleanGeminiResponse(finalResponse);


        log.info("Gemini 최종 응답 (파싱 전): {}", cleanedJsonString);

        JsonNode jsonNode = parseJson(cleanedJsonString);
        String topic = jsonNode.get("itemName").asText();

        ChatRoom newChatRoom = chatRoomRepository.save(new ChatRoom(topic));
        chatMessageRepository.save(new ChatMessage(newChatRoom, "ai", cleanedJsonString));

        return new StartChatResponse(newChatRoom.getId(), jsonNode);
    }
    @Transactional
    public ChatResponse postMessage(Long roomId, PostMessageRequest request,String language) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        chatMessageRepository.save(new ChatMessage(chatRoom, "user", request.getMessage()));

        // 특정 주제가 아닌, DB의 모든 가게 정보를 가져옴
        String marketInfo = dbInfoService.getAllMarketInfo();

        // Gemini에게 '모든 정보'를 참고자료로 하여 답변을 요청
        String aiResponse = geminiService.askToGeminiWithMessage(
                chatRoom.getTopic(),
                request.getMessage(),
                marketInfo,
                language
        );

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