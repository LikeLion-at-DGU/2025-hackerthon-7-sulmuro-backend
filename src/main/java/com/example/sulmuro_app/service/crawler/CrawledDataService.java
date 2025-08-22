package com.example.sulmuro_app.service.crawler;

import com.example.sulmuro_app.domain.market.CrawlerMenuItem;
import com.example.sulmuro_app.domain.market.CrawlerStore;
import com.example.sulmuro_app.repository.market.CrawlerMenuItemRepository;
import com.example.sulmuro_app.repository.market.CrawlerStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CrawledDataService {

    // 의존성을 새로운 Repository로 변경
    private final CrawlerStoreRepository crawlerStoreRepository;
    private final CrawlerMenuItemRepository crawlerMenuItemRepository;

    @Transactional
    public void saveCrawledData(List<Map<String, Object>> storeInfos) {
        if (storeInfos == null) return;

        for (Map<String, Object> storeInfo : storeInfos) {
            String storeName = (String) storeInfo.get("storeName");
            if (storeName == null || storeName.isBlank()) continue;

            CrawlerStore store = crawlerStoreRepository.findByName(storeName).orElseGet(() -> {
                CrawlerStore newStore = new CrawlerStore(storeName, "블로그 정보를 통해 수집된 가게입니다.", null);
                return crawlerStoreRepository.save(newStore);
            });

            List<Map<String, Object>> menus = (List<Map<String, Object>>) storeInfo.get("menus");
            if (menus != null) {
                for (Map<String, Object> menuInfo : menus) {
                    String menuName = (String) menuInfo.get("name");
                    Integer price = (menuInfo.get("price") instanceof Integer) ? (Integer) menuInfo.get("price") : null;

                    if (menuName != null && !menuName.isBlank() && !crawlerMenuItemRepository.existsByStoreAndName(store, menuName)) {
                        CrawlerMenuItem menuItem = new CrawlerMenuItem(store, menuName, price);
                        crawlerMenuItemRepository.save(menuItem);
                    }
                }
            }
        }
    }
}