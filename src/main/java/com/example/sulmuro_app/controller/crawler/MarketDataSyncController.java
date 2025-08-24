package com.example.sulmuro_app.controller.crawler;

import com.example.sulmuro_app.dto.bin.ApiResponse;
import com.example.sulmuro_app.service.crawler.BlogContentProcessorService;
import com.example.sulmuro_app.service.crawler.CrawledDataService;
import com.example.sulmuro_app.service.crawler.NaverBlogCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j // 로깅을 위해 추가
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class MarketDataSyncController {

    private final NaverBlogCrawlerService crawlerService;
    private final BlogContentProcessorService processorService;
    private final CrawledDataService dataService;

    @PostMapping("/market-data")
    public ApiResponse<String> syncMarketData() {
        final int CHUNK_SIZE = 20; // 한 번에 처리할 게시글 수
        final int TOTAL_COUNT = 200; // 총 가져올 게시글 수

        log.info("블로그 데이터 동기화를 시작합니다. 총 {}개, {}개씩 처리.", TOTAL_COUNT, CHUNK_SIZE);

        // 총 5번 반복 (100 / 20 = 5)
        for (int i = 0; i < (TOTAL_COUNT / CHUNK_SIZE); i++) {
            int start = (i * CHUNK_SIZE) + 1; // start 파라미터 계산 (1, 21, 41, 61, 81)
            log.info("... {}번째 청크 처리 (시작 위치: {})", i + 1, start);

            // 블로그 데이터 20개씩 크롤링
            List<String> descriptions = crawlerService.fetchBlogDescriptions("광장시장", CHUNK_SIZE, start);

            if(descriptions.isEmpty()){
                log.warn("... {}번째 청크에서 가져올 데이터가 없습니다. 중단합니다.", i + 1);
                break;
            }

            // 20개 데이터에서 정보 추출 (Gemini API 호출 1회)
            List<Map<String, Object>> storeInfos = processorService.processBlogContent(descriptions);

            // 추출된 정보를 DB에 저장
            dataService.saveCrawledData(storeInfos);


        }

        log.info("블로그 데이터 동기화가 성공적으로 완료되었습니다.");
        return ApiResponse.success("Market data synchronization completed successfully.", "OK");
    }
}