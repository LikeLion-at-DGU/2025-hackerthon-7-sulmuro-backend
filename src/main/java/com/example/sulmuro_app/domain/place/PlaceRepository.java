package com.example.sulmuro_app.domain.place;;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByPlaceIdIn(Collection<Long> ids);

}
