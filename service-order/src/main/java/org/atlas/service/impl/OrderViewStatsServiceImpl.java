package org.atlas.service.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atlas.model.OrderViewStats;
import org.atlas.repository.OrderViewStatsRepository;
import org.atlas.service.OrderViewStatsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderViewStatsServiceImpl implements OrderViewStatsService {

    private final OrderViewStatsRepository repository;

    @Override
    public void recordOrderView(Long orderId) {
        // Найдите существующую запись статистики или создайте новую
        OrderViewStats stats = repository.findByOrderId(orderId)
                .stream()
                .filter(s -> s.getDate().isEqual(LocalDate.now()))
                .findFirst()
                .orElse(OrderViewStats.builder()
                        .orderId(orderId)
                        .date(LocalDate.now())
                        .viewCount(0)
                        .build());

        // Увеличьте счетчик просмотров
        stats.setViewCount(stats.getViewCount() + 1);
        repository.save(stats);
    }

    @Override
    public List<OrderViewStats> getOrderViewStats(Long orderId) {
        return repository.findByOrderId(orderId).stream()
                .sorted(Comparator.comparing(OrderViewStats::getDate))
                .toList();
    }

    @Override
    public void deleteByOrderId(Long orderId) {
        repository.deleteByOrderId(orderId);
    }
}
