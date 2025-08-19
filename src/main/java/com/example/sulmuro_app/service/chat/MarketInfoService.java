package com.example.sulmuro_app.service.chat;

import com.example.sulmuro_app.domain.market.MenuItem;
import com.example.sulmuro_app.repository.market.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketInfoService {

    private final MenuItemRepository menuItemRepository;

    @Transactional(readOnly = true)
    public String findMarketInfoByItemName(String itemName) {
        List<MenuItem> menuItems = menuItemRepository.findWithStoreByItemName(itemName);

        if (menuItems.isEmpty()) {
            return "관련 가게 정보 없음";
        }

        // Gemini API가 이해하기 쉬운 텍스트 형태로 정보를 가공
        return menuItems.stream()
                .map(item -> {
                    // price가 null일 경우 "정보 없음"으로 처리
                    String priceInfo = item.getPrice() != null ? item.getPrice() + "원" : "정보 없음";

                    return String.format(
                        "가게명: %s, 메뉴: %s, 가격: %s, 가게소개: %s, 위치: %s",
                        item.getStore().getName(),
                        item.getName(),
                        priceInfo,
                        item.getStore().getNotes(),
                        item.getStore().getLocationDesc()
                    );
                })
                .collect(Collectors.joining("\n"));
    }
}