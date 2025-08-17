package com.example.sulmuro_app.service.chat;

import com.example.sulmuro_app.domain.chat.ChatMessage;
import com.example.sulmuro_app.domain.chat.ChatRoom;
import com.example.sulmuro_app.dto.chat.request.PostMessageRequest;
import com.example.sulmuro_app.dto.chat.response.ChatResponse;
import com.example.sulmuro_app.dto.chat.response.StartChatResponse;
import com.example.sulmuro_app.repository.chat.ChatMessageRepository;
import com.example.sulmuro_app.repository.chat.ChatRoomRepository;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;
    @Value("${gemini.model-name}")
    private String modelName;

    @Transactional
    public StartChatResponse startChat(MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어있습니다.");
        }

        // Gemini에 첫 질문
        String rawResponse = askToGeminiWithImage(imageFile);

        // Gemini 응답 가공 (Markdown 코드 블록 제거)
        String cleanedJsonString = cleanGeminiResponse(rawResponse);

        // 주제(itemName) 추출
        JsonNode jsonNode = objectMapper.readTree(cleanedJsonString);
        String topic = jsonNode.get("itemName").asText();

        // DB에 채팅방 및 첫 메시지 저장
        ChatRoom newChatRoom = chatRoomRepository.save(new ChatRoom(topic));
        chatMessageRepository.save(new ChatMessage(newChatRoom, "ai", cleanedJsonString));

        // 최종 응답 객체 생성
        return new StartChatResponse(newChatRoom.getId(), jsonNode);
    }
    // Gemini 응답 문자열을 정리하는 private 메서드
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
    @Transactional
    public ChatResponse postMessage(Long roomId, PostMessageRequest request) {
        // 채팅방 정보 가져오기
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        // 사용자 메시지 저장
        chatMessageRepository.save(new ChatMessage(chatRoom, "user", request.getMessage()));

        // Gemini에 후속 질문
        String aiResponse = askToGeminiWithMessage(chatRoom.getTopic(), request.getMessage());

        // AI 응답 저장
        chatMessageRepository.save(new ChatMessage(chatRoom, "ai", aiResponse));

        return new ChatResponse(aiResponse);
    }

    // 이미지와 함께 Gemini에 질문하는 메소드 (기존 로직과 거의 동일)
    private String askToGeminiWithImage(MultipartFile imageFile) throws IOException {
        String textPrompt = """
                당신은 광장시장 전문 음식 큐레이터입니다.
        아래 이미지를 보고 주요 음식 또는 사물을 한국어로 itemName에 정확히 입력해주세요.
        description에는 해당 itemName을 150자 내외로 설명해주세요.
        설명은 외국인이 itemName이 어떤 것인지 알 수 있을 정도로 설명해주세요.
        만약 광장시장 대표 음식(빈대떡, 육회, 마약김밥, 꼬마김밥 등)이거나 물건(꽃신,한복,홍삼 등)이라면 isGwangjangItem을 true로 하고, recommendedStores에는 시장 내 실제 가게 정보를 1~2개 배열로 담아주세요.
        광장시장과 관계 없는 경우 isGwangjangItem=false, recommendedStores는 빈 배열([])로, description 마지막에 '광장시장과는 관련이 없는 사진이네요.' 를 덧붙여주세요.
        추가로 recommendedquestion에는 itemName에 관련해서 추가로 물어볼만한 질문 3가지를 배열로 만들어주세요. 배열안에 각질문은 15자 이내로 해주세요.
        답변은 반드시 아래와 같은 JSON 형식으로 해주세요.

        # 응답 예시
        {
          "itemName": "빈대떡",
          "description": "녹두를 갈아 만든 한국 전통전으로 바삭한 식감과 담백한 맛이 특징입니다.",
          "isGwangjangItem": true,
          "recommendedStores": [
            { "name": "순희네 빈대떡", "notes": "언제나 사람이 많은 원조 맛집" },
            { "name": "박가네 빈대떡", "notes": "고기완자가 함께 유명해요" }
          ],
          "recommendedquestion": [
            { "question": "같이먹으면 좋은 음식은?" },
            { "question": "어떻게 먹는건가요?" },
            { "question": "먹을때 주의할점은?" },
        }
    """;
        byte[] imageBytes = imageFile.getBytes();
        Client client = Client.builder().apiKey(apiKey).build();

        Part textPart = Part.fromText(textPrompt);
        Part imagePart = Part.fromBytes(imageBytes, imageFile.getContentType());
        Content input = Content.builder().role("user").parts(List.of(textPart, imagePart)).build();

        GenerateContentResponse response = client.models.generateContent(modelName, List.of(input), null);
        log.info("Gemini 이미지 분석 답변 수신: {}", response.text());
        return response.text();
    }

    // 메시지(텍스트)로 Gemini에 질문하는 메소드 (새로 추가)
    private String askToGeminiWithMessage(String topic, String message) {
        String textPrompt = String.format("""
            당신은 광장시장 전문 음식 큐레이터입니다.
            '%s'와 '광장시장'이라는 주제와 관련해서 아래 질문에 대해 친절하고 상세하게 한국어로 답변해주세요.
            답변은 150자 내외로 해주세요.
            질문: %s
            """, topic, message);

        Client client = Client.builder().apiKey(apiKey).build();

        Content input = Content.builder().role("user").parts(Part.fromText(textPrompt)).build();

        GenerateContentResponse response = client.models.generateContent(modelName, List.of(input), null);
        log.info("Gemini 텍스트 답변 수신: {}", response.text());
        return response.text();
    }
}