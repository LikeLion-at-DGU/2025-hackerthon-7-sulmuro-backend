package com.example.sulmuro_app.repository.market;

import com.example.sulmuro_app.domain.market.CrawlerStore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CrawlerStoreRepository extends JpaRepository<CrawlerStore, Long> {
    Optional<CrawlerStore> findByName(String name);
}