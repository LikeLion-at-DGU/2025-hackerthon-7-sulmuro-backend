package com.example.sulmuro_app.repository.market;

import com.example.sulmuro_app.domain.market.CrawlerMenuItem;
import com.example.sulmuro_app.domain.market.CrawlerStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrawlerMenuItemRepository extends JpaRepository<CrawlerMenuItem, Long> {
    boolean existsByStoreAndName(CrawlerStore store, String name);

    @Query("SELECT mi FROM CrawlerMenuItem mi JOIN FETCH mi.store WHERE mi.name LIKE %:itemName%")
    List<CrawlerMenuItem> findWithStoreByItemName(@Param("itemName") String itemName);
}