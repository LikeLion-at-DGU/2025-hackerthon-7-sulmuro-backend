package com.example.sulmuro_app.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 1단계: 이미지에서 핵심 아이템 이름만 빠르게 추출
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
    public String askToGeminiWithImageAndContext(MultipartFile imageFile, String marketInfoContext, String language) throws IOException {
        String targetLanguage = getLanguageFullName(language);
        String priceFormattingInstruction;
        String storeFormattingInstruction;

        if ("Korean".equals(targetLanguage)) {
            priceFormattingInstruction = """
                    - `averagePrice`에 제공된 가게 정보의 가격들을 바탕으로 **"최소가격원 ~ 최대가격원" 형식의 시세**를 계산하여 문자열로 넣어주세요. 만약 제공된 정보에 가격이 없다면 **"NULL"**으로 설정합니다.
        """;
            storeFormattingInstruction = """
                    - `recommendedStores`에 **관련 메뉴를 판매하는 바로 그 가게들의 이름**을 정확히 채워주세요. (최대 3개)
        """;
        } else {
            priceFormattingInstruction = """
                    - For `averagePrice`, calculate the price range from the provided store information and format it as a string 'MIN_PRICEwon ~ MAX_PRICEwon' (e.g., 5000won). If no price information is available, set it to 'NULL'.
        """;
            storeFormattingInstruction = """
                    - For `recommendedStores`, list the names of the relevant stores. The name **must be in the format: 'Romanized Korean Name (Original Korean Name)'**. For example: 'Sunhuine Bindaetteok (순희네 빈대떡)'.
        """;
        }

        String textPrompt = String.format("""
        # 역할
        당신은 광장시장의 전문 큐레이터입니다. 주어진 이미지와 가게 정보를 바탕으로, 아래 규칙을 엄격하게 준수하여 JSON 응답을 생성해주세요. 최종 JSON 응답의 모든 텍스트는 반드시 %s(으)로 작성되어야 합니다.

        # 제공된 광장시장 가게 정보
        %s

        # 규칙 (매우 중요)
        1.  **언어**: 최종 JSON 응답의 모든 텍스트 필드(`itemName`, `description` 등)는 반드시 **%s**로 작성되어야 합니다.
        2.  **이미지 분석**: 이미지를 분석하여 `itemName`과 `description`을 작성합니다. `description`은 150자 이상 300자 이하로 작성해주세요.
        3.  **광장시장 관련성 판단**:
            - **CASE 1: `itemName`과 관련된 메뉴를 "# 제공된 광장시장 가게 정보"에서 찾을 수 있는 경우:**
             - **(중요!) `itemName`과 정확히 일치하는 메뉴가 없더라도, '아이스 커피'와 '커피'처럼 의미적으로나 카테고리적으로 관련된 메뉴를 판매하는 가게를 당신의 추론 능력을 발휘하여 찾아내야 합니다.**
             - `isGwangjangItem`을 `true`로 설정합니다.
%s
%s
            - **CASE 2: `itemName`이 광장시장과 관련은 있지만, "# 제공된 광장시장 가게 정보"에 해당하는 가게가 없는 경우:**
                - `isGwangjangItem`을 `true`로 설정합니다.
                - `averagePrice`를 "정보 없음"(으)로 설정합니다.
                - `recommendedStores`는 반드시 빈 배열 `[]`로 설정하세요. 절대로 가게 이름을 지어내지 마세요.
            - **CASE 3: `itemName`이 광장시장과 전혀 관련이 없는 경우 (예: 자동차, 컴퓨터):**
                - `isGwangjangItem`을 `false`로 설정합니다.
                - `averagePrice`를 "정보 없음"(으)로 설정합니다.
                - `recommendedStores`는 반드시 빈 배열 `[]`로 설정합니다.
                - `description`의 마지막에 **"이 사진은 광장시장과 관련이 없는 것 같습니다."** 라는 문장을 반드시 추가해주세요.
        4.  **추천 질문**: `itemName`과 관련된 `recommendedquestion`을 3개 생성합니다.
        5.  **출력 형식**: 응답은 다른 설명 없이, 아래 예시와 같은 순수 JSON 객체 형식이어야 합니다.
            # 응답예시
            {
              "itemName": "Bindaetteok",
              "description": "A traditional Korean pancake made from ground mung beans, known for its crispy texture and savory taste.",
              "isGwangjangItem": true,
              "averagePrice": "5000won ~ 6000won",
              "recommendedStores": [
                 { "name": "Sunhuine Bindaetteok (순희네 빈대떡)" },
                 { "name": "Parkgane Bindaetteok (박가네 빈대떡)" }
              ],
              "recommendedquestion": [
                { "question": "What food goes well with it?" },
                { "question": "How should I eat it?" },
                { "question": "Any precautions when eating?" }
              ]
            }
            """, targetLanguage,marketInfoContext, targetLanguage, priceFormattingInstruction, storeFormattingInstruction);

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
     * DB 정보와 함께 텍스트 메시지로 Gemini에 후속 질문을 보냄
     */
    public String askToGeminiWithMessage(String topic, String message, String marketInfoContext, String language) {
        String targetLanguage = getLanguageFullName(language);
        String unrelatedMessage = switch (targetLanguage.toLowerCase()) {
            case "english" -> "The content of your question does not seem to be related to Gwangjang Market.";
            case "chinese" -> "您所提问的内容似乎与广藏市场无关。";
            default -> "질문하신 내용은 광장시장과는 관련이 없는것 같습니다.";
        };
        String textPrompt = String.format("""
        # 역할
        당신은 광장시장에 대해 모든 것을 아는 박식한 AI 가이드입니다. 당신의 임무는 사용자의 질문에 최대한 유용하고 친절하게 답변하는 것입니다.

        # 대화의 시작 주제
        - %s

        # 참고자료: 광장시장 전체 가게 DB 정보
        ```
        %s
        ```

        # 임무
        '# 사용자의 마지막 질문'의 의도를 파악하고, '# 참고자료'와 당신의 폭넓은 지식을 모두 활용하여 답변하세요.

        # 답변 규칙 (반드시 지켜주세요)
        1.  **질문 의도 파악이 최우선**: 사용자의 마지막 질문은 '# 대화의 시작 주제'와 전혀 다른 새로운 내용일 수 있습니다. 이 점을 반드시 인지하고 질문의 핵심 의도에 집중하세요.
        2.  **답변 생성 로직**:
            - **CASE 1: 질문이 '# 참고자료'와 관련 있을 때:** '# 참고자료'에 있는 정보를 우선적으로 활용하여 답변을 구성하세요.
            - **CASE 2: 질문이 '# 참고자료'와 관련 없을 때 (대화 주제가 바뀌었을 때):** '# 참고자료'의 내용에 얽매이지 말고, 당신의 폭넓은 자체 지식을 활용하여 질문에 직접 답변하세요. 광장시장에 대한 정보라면 무엇이든 좋습니다. 예를 들어 사용자가 '카페'를 물어보면, 당신이 아는 광장시장의 카페를 추천해야 합니다.
        3.  **관련성 판단**: 만약 사용자의 질문이 광장시장과 전혀 관련이 없다고 판단되면, 당신의 판단에 따라 자유롭게 답변하되, 응답의 맨 마지막에 다음 문장을 **반드시 정확하게** 추가해주세요: "%s"
        4.  **언어 및 길이 (매우 중요)**: 전체 답변은 반드시 **%s**로 작성해야 하며, **200자 이내로 매우 짧고 간결하게 요약해야 합니다.**
        5.  **형식 주의**: 답변은 꾸밈없는 순수 텍스트(Plain Text)여야 합니다. 절대로 마크다운(`**`, `*` 등)을 사용하지 마세요.

        # 사용자의 마지막 질문
        %s
        """, topic, marketInfoContext, unrelatedMessage, targetLanguage, message);

        Client client = Client.builder().apiKey(apiKey).build();
        Content input = Content.builder().role("user").parts(Part.fromText(textPrompt)).build();
        GenerateContentResponse response = client.models.generateContent(modelName, List.of(input), null);
        log.info("Gemini 텍스트 답변 수신: {}", response.text());
        return response.text();
    }

    private String getLanguageFullName(String code) {
        return switch (code.toLowerCase()) {
            case "ko" -> "Korean";
            case "en" -> "English";
            case "zh" -> "Chinese";
            default -> "English"; // 기본값
        };
    }
    /**
     * 블로그 본문 텍스트에서 가게와 메뉴 정보를 추출하여 JSON 형태로 반환
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


    /**
     * 단순 텍스트 번역을 요청
     */
    public String translateText(String sourceLang, String targetLang, String text) {
        String prompt = String.format(
                "당신은 전문 번역가입니다. 다음 텍스트를 '%s'(으)로부터 '%s'(으)로 번역해주세요. 다른 설명 없이 번역된 문장만 알려주세요.\n\ntext: \"%s\"",
                sourceLang, targetLang, text
        );

        Client client = Client.builder().apiKey(apiKey).build();
        Content input = Content.builder().role("user").parts(Part.fromText(prompt)).build();
        GenerateContentResponse response = client.models.generateContent(modelName, List.of(input), null);

        log.info("Gemini 번역 수신: {}", response.text());
        return response.text().trim();
    }

    /**
     * 번역과 함께 추천 표현 2개를 JSON 형식으로 요청
     */
    public String translateTextWithRecommendations(String sourceLang, String targetLang, String text) {

        String prompt = String.format("""
            # 역할
            당신은 한국의 전통 시장, 특히 광장 시장의 베테랑 상인 번역전문가입니다. 당신의 임무는 외국인 관광객이 시장에서 원활하게 소통할 수 있도록 돕는 것입니다. 친절하고 실용적인 표현을 알려주세요.

            # 임무
            1.  아래의 '# 사용자 문장'을 '%s'에서 '%s'(으)로 자연스럽게 번역합니다.
            2.  실제 시장 상황에서 유용한 **추천 표현 2개**를 제안합니다. 각 추천 표현은 반드시 원본 언어('%s')와 번역된 언어('%s')를 모두 포함해야 합니다.
                - **추천 표현 조건 (매우 중요):**
                    - 원래 문장의 의도와 같거나, 그 상황에서 추가로 물어보면 좋은 질문이어야 합니다.
                    - 원래 문장의 의도에서 벗어나면 안됩니다!
                    - 한국어(ko)로 번역할때는 더 정겹거나, 흥정을 유도하거나, 상인과 상호작용할 수 있는 표현을 우선적으로 제안해주세요.
            3.  결과는 반드시 아래 '# 출력 형식'과 동일한 JSON 객체로만 반환해야 합니다. 다른 설명은 절대 추가하지 마세요.

            # 사용자 문장
            "%s"

            # 출력 형식
            {
              "translatedText": "번역된 문장",
              "recommendations": [
                {
                  "source": "추천 표현 1의 원본 언어('%s') 문장",
                  "target": "추천 표현 1의 번역된 언어('%s') 문장"
                },
                {
                  "source": "추천 표현 2의 원본 언어('%s') 문장",
                  "target": "추천 표현 2의 번역된 언어('%s') 문장"
                }
              ]
            }
            """, sourceLang, targetLang, sourceLang, targetLang, text, sourceLang, targetLang, sourceLang, targetLang);

        Client client = Client.builder().apiKey(apiKey).build();
        Content input = Content.builder().role("user").parts(Part.fromText(prompt)).build();
        GenerateContentResponse response = client.models.generateContent(modelName, List.of(input), null);

        log.info("Gemini 번역 및 추천 표현 수신: {}", response.text());
        return cleanGeminiJsonResponse(response.text());

    }

// JSON 응답을 깔끔하게 정리하는 헬퍼 메서드
private String cleanGeminiJsonResponse(String rawResponse) {
    String cleaned = rawResponse.trim();
    if (cleaned.startsWith("```json")) {
        cleaned = cleaned.substring(7);
    }
    if (cleaned.endsWith("```")) {
        cleaned = cleaned.substring(0, cleaned.length() - 3);
    }
    return cleaned.trim();
}
}