package com.example.sulmuro_app.service.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class NaverBlogCrawlerService {

    @Value("${naver.api.client-id}")
    private String clientId;
    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private final String BLOG_SEARCH_URL = "https://openapi.naver.com/v1/search/blog.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> fetchBlogDescriptions(String query, int display, int start) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = BLOG_SEARCH_URL + "?query=" + query + "&display=" + display + "&start=" + start;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<String> descriptions = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            for (JsonNode item : root.path("items")) {
                String description = item.path("description").asText().replaceAll("<[^>]*>", "");
                descriptions.add(description);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return descriptions;
    }
}