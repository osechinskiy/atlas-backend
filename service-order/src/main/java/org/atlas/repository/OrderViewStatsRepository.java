package org.atlas.repository;

import java.util.List;
import org.atlas.model.OrderViewStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderViewStatsRepository extends JpaRepository<OrderViewStats, Long> {
    List<OrderViewStats> findByOrderId(Long orderId);

    void deleteByOrderId(Long orderId);
}
