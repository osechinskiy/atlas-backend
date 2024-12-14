package org.atlas.service.impl;

import org.atlas.model.OrderViewStats;
import org.atlas.repository.OrderViewStatsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderViewStatsServiceImplTest {

    @Mock
    private OrderViewStatsRepository repository;

    @InjectMocks
    private OrderViewStatsServiceImpl service;

    @Test
    @Order(1)
    @DisplayName("Должен записывать просмотр заказа")
    void recordOrderView_Positive() {
        Long orderId = 1L;
        LocalDate today = LocalDate.now();

        OrderViewStats existingStats = OrderViewStats.builder()
                .orderId(orderId)
                .date(today)
                .viewCount(1)
                .build();

        when(repository.findByOrderId(orderId)).thenReturn(List.of(existingStats));
        when(repository.save(any(OrderViewStats.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.recordOrderView(orderId);

        ArgumentCaptor<OrderViewStats> captor = ArgumentCaptor.forClass(OrderViewStats.class);
        verify(repository).save(captor.capture());

        OrderViewStats savedStats = captor.getValue();
        assertEquals(orderId, savedStats.getOrderId());
        assertEquals(today, savedStats.getDate());
        assertEquals(2, savedStats.getViewCount());
    }

    @Test
    @Order(2)
    @DisplayName("Должен создавать новую запись статистики, если она не существует")
    void recordOrderView_NewStats() {
        Long orderId = 1L;
        LocalDate today = LocalDate.now();

        when(repository.findByOrderId(orderId)).thenReturn(new ArrayList<>());
        when(repository.save(any(OrderViewStats.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.recordOrderView(orderId);

        ArgumentCaptor<OrderViewStats> captor = ArgumentCaptor.forClass(OrderViewStats.class);
        verify(repository).save(captor.capture());

        OrderViewStats savedStats = captor.getValue();
        assertEquals(orderId, savedStats.getOrderId());
        assertEquals(today, savedStats.getDate());
        assertEquals(1, savedStats.getViewCount());
    }

    @Test
    @Order(3)
    @DisplayName("Должен возвращать статистику просмотров заказа")
    void getOrderViewStats_Positive() {
        Long orderId = 1L;
        List<OrderViewStats> statsList = List.of(
                OrderViewStats.builder().orderId(orderId).date(LocalDate.now().minusDays(1)).viewCount(1).build(),
                OrderViewStats.builder().orderId(orderId).date(LocalDate.now()).viewCount(2).build()
        );

        when(repository.findByOrderId(orderId)).thenReturn(statsList);

        List<OrderViewStats> result = service.getOrderViewStats(orderId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getViewCount());
        assertEquals(2, result.get(1).getViewCount());
    }

    @Test
    @Order(4)
    @DisplayName("Должен удалять статистику просмотров по ID заказа")
    void deleteByOrderId_Positive() {
        Long orderId = 1L;

        doNothing().when(repository).deleteByOrderId(orderId);

        service.deleteByOrderId(orderId);

        verify(repository, times(1)).deleteByOrderId(orderId);
    }

}