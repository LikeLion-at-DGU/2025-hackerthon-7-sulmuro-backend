// src/main/java/com/example/sulmuro_app/service/crawler/MarketDataScheduler.java (새 파일)
package com.example.sulmuro_app.service.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataScheduler {

    private final NaverBlogCrawlerService crawlerService;
    private final BlogContentProcessorService processorService;
    private final CrawledDataService dataService;

    /**
     * 매월 1일 새벽 4시에 자동으로 실행
     * cron = "[초] [분] [시] [일] [월] [요일]"
     */
    @Scheduled(cron = "0 0 4 1 * *") // 매월 1일 새벽 4시 크롤링동작
    public void syncMarketDataMonthly() {
        final int CHUNK_SIZE = 20;
        final int TOTAL_COUNT = 200;

        log.info("🚀 [스케줄러] 월간 블로그 데이터 동기화를 시작합니다.");

        for (int i = 0; i < (TOTAL_COUNT / CHUNK_SIZE); i++) {
            int start = (i * CHUNK_SIZE) + 1;
            log.info("... {}번째 청크 처리 (시작 위치: {})", i + 1, start);

            List<String> descriptions = crawlerService.fetchBlogDescriptions("광장시장", CHUNK_SIZE, start);

            if (descriptions.isEmpty()) {
                log.warn("... {}번째 청크에서 가져올 데이터가 없어 동기화를 중단합니다.", i + 1);
                break;
            }

            List<Map<String, Object>> storeInfos = processorService.processBlogContent(descriptions);
            dataService.saveCrawledData(storeInfos);
        }

        log.info("✅ [스케줄러] 월간 블로그 데이터 동기화가 성공적으로 완료되었습니다.");
    }
}