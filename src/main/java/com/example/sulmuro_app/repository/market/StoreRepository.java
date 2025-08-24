package com.example.sulmuro_app.repository.market;

import com.example.sulmuro_app.domain.market.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    // N+1 문제를 방지하기 위해 JPQL의 fetch join을 사용하여 가게와 메뉴를 한 번에 조회합니다.
    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.menuItems")
    List<Store> findAllWithMenuItems();
}