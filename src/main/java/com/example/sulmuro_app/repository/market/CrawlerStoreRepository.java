package com.example.sulmuro_app.repository.market;

import com.example.sulmuro_app.domain.market.CrawlerStore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface CrawlerStoreRepository extends JpaRepository<CrawlerStore, Long> {
    Optional<CrawlerStore> findByName(String name);


    @Query("SELECT cs FROM CrawlerStore cs LEFT JOIN FETCH cs.menuItems")
    List<CrawlerStore> findAllWithMenuItems();
}