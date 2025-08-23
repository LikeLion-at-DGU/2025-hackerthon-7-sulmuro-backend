package com.example.sulmuro_app.i18n.mt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import java.util.List;

@Component
public class DeepLClient implements MTClient {

    private final RestClient client;
    private final String formality;

    public DeepLClient(RestClient.Builder builder,
                       @Value("${deepl.baseUrl}") String baseUrl,
                       @Value("${deepl.authKey}") String authKey,
                       @Value("${deepl.formality:default}") String formality) {
        this.client = builder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "DeepL-Auth-Key " + authKey)
                .build();
        this.formality = formality;
    }

    @Override
    public String translate(String text, String sourceLangOrNull, String targetLang) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("text", text);
        form.add("target_lang", targetLang);             // EN | KO | ZH
        if (sourceLangOrNull != null && !sourceLangOrNull.isBlank()) {
            form.add("source_lang", sourceLangOrNull);   // EN | KO | ZH (없으면 DeepL 감지)
        }
        if (!"default".equalsIgnoreCase(formality)) {
            form.add("formality", formality);
        }

        DeepLResponse resp = client.post()
                .uri("/v2/translate")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(DeepLResponse.class);

        if (resp == null || resp.getTranslations() == null || resp.getTranslations().isEmpty()) {
            return text; // 안전 폴백
        }
        return resp.getTranslations().get(0).getText();
    }

    @Data
    public static class DeepLResponse {
        private List<Item> translations;
        @Data public static class Item {
            private String detected_source_language;
            private String text;
        }
    }
}
