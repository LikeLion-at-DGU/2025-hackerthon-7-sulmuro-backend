package com.example.sulmuro_app.service.chat;

import com.example.sulmuro_app.domain.market.CrawlerStore;
import com.example.sulmuro_app.domain.market.Store;
import com.example.sulmuro_app.repository.market.CrawlerStoreRepository;
import com.example.sulmuro_app.repository.market.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DBInfoService {

    private final StoreRepository storeRepository;
    private final CrawlerStoreRepository crawlerStoreRepository;

    @Transactional(readOnly = true)
    public String getAllMarketInfo() {
        // 1. 수동으로 입력된 모든 가게 정보와 메뉴를 가져옴
        List<Store> manualStores = storeRepository.findAllWithMenuItems();

        // 2. 크롤링으로 수집된 모든 가게 정보와 메뉴를 가져옴
        List<CrawlerStore> crawledStores = crawlerStoreRepository.findAllWithMenuItems();

        // 3. 각 정보를 보기 좋은 문자열 형태로 변환
        String manualInfo = manualStores.stream()
                .map(this::formatStoreInfo)
                .collect(Collectors.joining("\n"));

        String crawledInfo = crawledStores.stream()
                .map(this::formatCrawlerStoreInfo)
                .collect(Collectors.joining("\n"));

        // 4. 두 정보를 하나로 합쳐서 반환
        return Stream.of(manualInfo, crawledInfo)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining("\n---\n"));
    }

    // Store 엔티티를 문자열로 변환하는 헬퍼 메서드
    private String formatStoreInfo(Store store) {
        String menuDetails = store.getMenuItems().stream()
                .map(item -> String.format("%s(%s)", item.getName(), item.getPrice() != null ? item.getPrice() + "원" : "가격 정보 없음"))
                .collect(Collectors.joining(", "));
        return String.format(
                "가게명: %s, 가게소개: %s, 위치: %s, 메뉴들: [%s]",
                store.getName(), store.getNotes(), store.getLocationDesc(), menuDetails
        );
    }

    // CrawlerStore 엔티티를 문자열로 변환하는 헬퍼 메서드
    private String formatCrawlerStoreInfo(CrawlerStore store) {
        String menuDetails = store.getMenuItems().stream()
                .map(item -> String.format("%s(%s)", item.getName(), item.getPrice() != null ? item.getPrice() + "원" : "가격 정보 없음"))
                .collect(Collectors.joining(", "));
        return String.format(
                "가게명: %s, 가게소개: %s, 위치: %s, 메뉴들: [%s]",
                store.getName(), store.getNotes(), store.getLocationDesc(), menuDetails
        );
    }
}