package com.example.sulmuro_app.service.chat;

import com.example.sulmuro_app.dto.chat.response.ChatResponse;
import com.google.genai.Client;
import com.google.genai.types.Part;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Content;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    @Value("${gemini.api-key}")
    private String apiKey;
    @Value("${gemini.model-name}")
    private String modelName;

    public ChatResponse getAiResponse(MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            return new ChatResponse("이미지 파일이 없어요. 사진을 먼저 등록해주세요! 📸");
        }
        String geminiAnswer = askToGeminiWithImage(imageFile);
        return new ChatResponse(geminiAnswer);
    }

    private String askToGeminiWithImage(MultipartFile imageFile) throws IOException {
        String textPrompt = """
            당신은 광장시장 전문 음식 큐레이터입니다.
            아래 이미지를 보고 주요 음식 또는 사물을 한국어로 itemName에 정확히 입력해주세요.
            description에는 해당 itemName을 150자 내외로 설명해주세요.
            설명은 외국인이 itemName이 어떤것인지 알수있을 정도로 설명해주세요.
            만약 광장시장 대표 음식(빈대떡, 육회, 마약김밥, 꼬마김밥 등)이라면 isGwangjangItem을 true로 하고, recommendedStore에는 시장 내 실제 가게나 예시 가게 이름을 1~2개 적어주세요.
            광장시장과 관계 없는 경우 isGwangjangItem=false, recommendedStore=""로, description 마지막에 '광장시장과는 관련이 없는 사진이네요.' 를 덧붙여주세요.
            답변은 반드시 아래와 같은 JSON 형식으로 해주세요.

            {
              "itemName": "빈대떡",
              "description": "녹두를 갈아 만든 한국 전통전으로 바삭한 식감과 담백한 맛이 특징입니다.",
              "isGwangjangItem": true,
              "recommendedStore": "순희네 빈대떡, 박가네 빈대떡"
            }
        """;

        byte[] imageBytes = imageFile.getBytes();
        Client client = Client.builder().apiKey(apiKey).build();

        Part textPart = Part.fromText(textPrompt);
        Part imagePart = Part.fromBytes(imageBytes, imageFile.getContentType());

        Content input = Content.builder()
                .role("user")      // 필요하다면, AI 사용자 역할
                .parts(List.of(textPart, imagePart))
                .build();

        GenerateContentResponse response = client.models.generateContent(
                modelName,
                List.of(input),    // List<Content>
                null               // config 없으면 null
        );

        String resultText = response.text();
        log.info("Gemini 답변 수신: {}", resultText);
        return resultText;
    }
}
