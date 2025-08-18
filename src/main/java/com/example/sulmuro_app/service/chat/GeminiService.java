package com.example.sulmuro_app.service.chat;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api-key}")
    private String apiKey;
    @Value("${gemini.model-name}")
    private String modelName;

    /**
     * 이미지와 함께 Gemini에 첫 질문을 보냅니다.
     */
    public String askToGeminiWithImage(MultipartFile imageFile) throws IOException {
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

    /**
     * 텍스트 메시지로 Gemini에 후속 질문을 보냅니다.
     */
    public String askToGeminiWithMessage(String topic, String message) {
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