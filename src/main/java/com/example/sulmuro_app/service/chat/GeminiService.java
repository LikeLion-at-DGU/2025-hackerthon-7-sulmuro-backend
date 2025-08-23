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
     * 1단계: 이미지에서 핵심 아이템 이름만 빠르게 추출합니다.
     */
    public String extractItemNameFromImage(MultipartFile imageFile) throws IOException {
        String textPrompt = "당신은 광장시장 전문 음식 큐레이터입니다. 이 이미지에 보이는 가장 핵심적인 음식 또는 사물의 이름을 한 단어의 명사로만 말해줘. 광장시장에 파는 음식혹은 사물 등 광장시장에 관련이 있을 확률이 높아. 예를 들어 '떡볶이', '육회', '한복' ,'빈대떡' 과 같이 답변해줘.";

        byte[] imageBytes = imageFile.getBytes();
        Client client = Client.builder().apiKey(apiKey).build();

        Part textPart = Part.fromText(textPrompt);
        Part imagePart = Part.fromBytes(imageBytes, imageFile.getContentType());
        Content input = Content.builder().role("user").parts(List.of(textPart, imagePart)).build();

        GenerateContentResponse response = client.models.generateContent(modelName, List.of(input), null);
        String itemName = response.text().trim();
        log.info("Gemini 1차 이미지 분석 (itemName 추출): {}", itemName);
        return itemName;
    }

    /**
     * 2단계: 이미지와 DB 컨텍스트를 모두 사용하여 최종 답변을 생성
     */
    public String askToGeminiWithImageAndContext(MultipartFile imageFile, String marketInfoContext) throws IOException {
        String textPrompt = String.format("""
                당신은 광장시장의 모든 것을 아는 전문 큐레이터입니다. 주어진 이미지와 가게 정보를 바탕으로 다음 규칙을 엄격하게 준수하여 JSON 응답을 생성해주세요.

                # 제공된 실제 광장시장 가게 정보
                %s

                # 규칙 (매우 중요)
                1.  먼저, 이미지를 분석하여 `itemName`과 `description`을 작성합니다. `description`은 200자 이상 400자 이하로 답변해주세요
                2.  다음으로, `itemName`이 "광장시장"과 관련이 있는지 판단합니다. (예: 빈대떡, 마약김밥, 육회 등 음식, 한복 등)
                    - **CASE 1: `itemName`이 광장시장과 관련 있고, "# 제공된 실제 광장시장 가게 정보"에 해당 가게가 있는 경우:**
                        - `isGwangjangItem`을 `true`로 설정합니다.
                        - `averagePrice`에 제공된 가게 정보의 가격들을 바탕으로 **"최소가격원 ~ 최대가격원" 형식의 시세**를 계산하여 문자열로 넣어주세요. 만약 제공된 정보에 가격이 없다면 **"정보 없음"**으로 설정합니다.
                        - `recommendedStores`에 **제공된 가게 정보**를 바탕으로 가게 이름을 정확히 채워주세요. (최대 3개)
                    - **CASE 2: `itemName`이 광장시장과 관련은 있지만, "# 제공된 실제 광장시장 가게 정보"에 해당 가게가 없는 경우 (정보가 "관련 가게 정보 없음"으로 제공된 경우):**
                        - `isGwangjangItem`을 `true`로 설정합니다.
                        - `averagePrice`를 **"정보 없음"**으로 설정합니다.
                        - `recommendedStores`를 **반드시 빈 배열 `[]`** 로 설정합니다. 절대로 가게 정보를 지어내지 마세요.
                    - **CASE 3: `itemName`이 광장시장과 전혀 관련이 없는 경우 (예: 자동차, 컴퓨터):**
                        - `isGwangjangItem`을 `false`로 설정합니다.
                        - `averagePrice`를 **"정보 없음"**으로 설정합니다.
                        - `recommendedStores`를 **반드시 빈 배열 `[]`** 로 설정합니다.
                        - `description`의 마지막에 **"광장시장과는 관련이 없는 사진인것 같습니다."** 라는 문장을 반드시 추가해주세요.
                3.  마지막으로, `itemName`과 관련된 `recommendedquestion`을 3개 생성합니다.
                4.  응답은 반드시 아래 예시와 같은 JSON 형식이어야 하며, 다른 설명 없이 JSON 객체만 반환해야 합니다.

                # 응답 예시
                {
                  "itemName": "빈대떡",
                  "description": "녹두를 갈아 만든 한국 전통전으로 바삭한 식감과 담백한 맛이 특징입니다.",
                  "isGwangjangItem": true,
                  "averagePrice": "5,000원 ~ 6,000원",
                  "recommendedStores": [
                     { "name": "순희네 빈대떡" },
                     { "name": "박가네 빈대떡" }
                  ],
                  "recommendedquestion": [
                    { "question": "같이먹으면 좋은 음식은?" },
                    { "question": "어떻게 먹는건가요?" },
                    { "question": "먹을때 주의할점은?" }
                  ]
                }
            """, marketInfoContext);


        byte[] imageBytes = imageFile.getBytes();
        Client client = Client.builder().apiKey(apiKey).build();

        Part textPart = Part.fromText(textPrompt);
        Part imagePart = Part.fromBytes(imageBytes, imageFile.getContentType());
        Content input = Content.builder().role("user").parts(List.of(textPart, imagePart)).build();

        GenerateContentResponse response = client.models.generateContent(modelName, List.of(input), null);
        log.info("Gemini 2차 이미지 분석 (최종 답변 생성) 수신");
        return response.text();
    }

    /**
     * DB 정보와 함께 텍스트 메시지로 Gemini에 후속 질문을 보냅니다.
     */
    public String askToGeminiWithMessage(String topic, String message, String marketInfoContext) {
        String textPrompt = String.format("""
        당신은 광장시장의 모든 정보를 담고 있는 챗봇입니다. 당신이 가장먼저 참고할 정보는 '# 제공된 실제 광장시장 정보'입니다.

        # 제공된 실제 광장시장 정보
        %s

        # 대화의 시작 주제
        - %s

        # 당신의 임무 (매우 중요)
        사용자의 '# 마지막 질문'에 답변해야 합니다. 답변은 '# 제공된 실제 광장시장 정보'를 최우선으로 해야 합니다.

        # 답변 규칙
        1.  사용자가 질문한 가게나 메뉴가 '# 제공된 실제 광장시장 정보'에 있는지 확인합니다.
        2.  만약 정보가 있다면, 그 정보를 사용하여 질문에 답변합니다.
        3.  만약 정보가 없다면, 다른 말을 하지 말고 **정확히 "죄송하지만, 요청하신 내용에 대한 정보는 찾을 수 없습니다."** 라고만 답변해야 합니다. 절대로 정보를 추측하거나 지어내지 마세요.
        4.  (매우 중요) 모든 답변은 150자 이내의 한국어 평문(plaintext)으로 작성하세요.

        # 마지막 질문
        %s
        """, marketInfoContext, topic, message);

        Client client = Client.builder().apiKey(apiKey).build();
        Content input = Content.builder().role("user").parts(Part.fromText(textPrompt)).build();
        GenerateContentResponse response = client.models.generateContent(modelName, List.of(input), null);
        log.info("Gemini 텍스트 답변 수신: {}", response.text());
        return response.text();
    }
    /**
     * 블로그 본문 텍스트에서 가게와 메뉴 정보를 추출하여 JSON 형태로 반환합니다. (정확도 개선 버전)
     */
    public String extractMarketInfoFromText(String blogContent) {
        String textPrompt = String.format("""
            당신은 텍스트에서 광장시장의 맛집 정보를 추출하는 매우 꼼꼼한 데이터 분석가입니다.
            주어진 블로그 본문에서 다음 규칙을 **가장 엄격하게** 준수하여 가게와 메뉴 정보를 JSON 배열 형태로 추출해주세요.

            # 블로그 본문
            %s

            # 규칙 (매우 중요)
            1.  `storeName`은 **반드시 실제 가게의 상호(고유 명사)**여야 합니다.
                - **좋은 예:** "순희네 빈대떡", "원조누드치즈김밥", "광장시장 찹쌀꽈배기", "박가네"
                - **나쁜 예 (절대 포함 금지):** "빈대떡", "김밥", "꽈배기", "육회"와 같은 단순 음식 이름(일반 명사)은 가게 이름이 될 수 없습니다.
            2.  만약 본문에서 "순희네 빈대떡"이라는 가게가 언급되었다면, `storeName`은 **"순희네 빈대떡"** 이 되어야 하며, 절대 "빈대떡"이 되어서는 안 됩니다.
            3.  메뉴는 `menus` 배열 안에 `name` (메뉴 이름)과 `price` (가격)으로 구성됩니다. 가격 정보가 없으면 price는 `null`로 설정하세요.
            4.  본문에 언급된 **실제 가게만**을 대상으로, 최대한 많은 정보를 정확하게 추출해주세요. 없는 정보는 절대 지어내지 마세요.
            5.  응답은 다른 설명 없이, 아래 예시와 같은 순수 JSON 배열 형식이어야 합니다.

            # 응답 예시
            [
              {
                "storeName": "순희네 빈대떡",
                "menus": [
                  { "name": "빈대떡", "price": 5000 },
                  { "name": "고기완자", "price": 3000 }
                ]
              },
              {
                "storeName": "광장시장 찹쌀꽈배기",
                "menus": [
                  { "name": "찹쌀꽈배기", "price": 1000 },
                  { "name": "팥도너츠", "price": null }
                ]
              }
            ]
            """, blogContent);

        Client client = Client.builder().apiKey(apiKey).build();
        Content input = Content.builder().role("user").parts(Part.fromText(textPrompt)).build();
        GenerateContentResponse response = client.models.generateContent(modelName, List.of(input), null);
        log.info("Gemini 텍스트 기반 정보 추출 수신 (정확도 개선)");

        // JSON 응답 클리닝 로직
        String cleaned = response.text().trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

}