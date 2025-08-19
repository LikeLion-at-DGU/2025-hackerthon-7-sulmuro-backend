package com.example.sulmuro_app.repository.market;

import com.example.sulmuro_app.domain.market.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    // 메뉴 이름으로 검색 시, 연관된 Store 정보까지 한번에 조회 (N+1 문제 방지)
    @Query("SELECT mi FROM MenuItem mi JOIN FETCH mi.store WHERE mi.name LIKE %:itemName%")
    List<MenuItem> findWithStoreByItemName(@Param("itemName") String itemName);
}