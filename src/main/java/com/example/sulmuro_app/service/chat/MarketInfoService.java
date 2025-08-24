package com.example.sulmuro_app.service.chat;

import com.example.sulmuro_app.domain.market.CrawlerMenuItem;
import com.example.sulmuro_app.domain.market.MenuItem;
import com.example.sulmuro_app.repository.market.CrawlerMenuItemRepository;
import com.example.sulmuro_app.repository.market.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MarketInfoService {

    private final MenuItemRepository menuItemRepository;
    private final CrawlerMenuItemRepository crawlerMenuItemRepository; 

    @Transactional(readOnly = true)
    public String findMarketInfoByItemName(String itemName) {
        // 1. 수동으로 입력한 DB에서 정보 조회
        List<MenuItem> manualItems = menuItemRepository.findWithStoreByItemName(itemName);

        // 2. 블로그에서 크롤링한 DB에서 정보 조회
        List<CrawlerMenuItem> crawledItems = crawlerMenuItemRepository.findWithStoreByItemName(itemName);

        if (manualItems.isEmpty() && crawledItems.isEmpty()) {
            return "관련 가게 정보 없음";
        }

        // 3. 두 소스의 정보를 하나의 문자열로 통합
        String manualInfo = manualItems.stream()
                .map(item -> formatInfo(
                        item.getStore().getName(),
                        item.getName(),
                        item.getPrice(),
                        item.getStore().getNotes(),
                        item.getStore().getLocationDesc()
                ))
                .collect(Collectors.joining("\n"));

        String crawledInfo = crawledItems.stream()
                .map(item -> formatInfo(
                        item.getStore().getName(),
                        item.getName(),
                        item.getPrice(),
                        item.getStore().getNotes(),
                        item.getStore().getLocationDesc()
                ))
                .collect(Collectors.joining("\n"));

        // 두 정보 사이에 구분선을 넣어 합침
        return Stream.of(manualInfo, crawledInfo)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining("\n---\n"));
    }

    // 정보 포맷팅 로직을 공통 메서드로 분리
    private String formatInfo(String storeName, String menuName, Integer price, String notes, String location) {
        String priceInfo = price != null ? price + "원" : "정보 없음";
        return String.format(
                "가게명: %s, 메뉴: %s, 가격: %s, 가게소개: %s, 위치: %s",
                storeName, menuName, priceInfo, notes, location
        );
    }
}