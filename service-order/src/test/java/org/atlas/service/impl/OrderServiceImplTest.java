package org.atlas.service.impl;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.atlas.exception.OrderNotFoundException;
import org.atlas.mapper.OrderMapper;
import org.atlas.model.Order;
import org.atlas.repository.OrderRepository;
import org.atlas.rest.dto.NewOrderRequest;
import org.atlas.model.dto.OrderDto;
import org.atlas.rest.dto.SimpleUserInfoResponse;
import org.atlas.service.OrderViewStatsService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderViewStatsService orderViewStatsService;

    @Mock
    private EurekaClient discoveryClient;

    @Mock
    private RestClient restClient;

    @Mock
    private InstanceInfo instanceInfo;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;


    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Должен находить заказ по его Id")
    void shouldFindOrderById_Positive() {
        Order order = new Order();
        order.setUserId(1L);
        order.setPhoneId(1L);
        order.setTitle("Test Order");

        RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(repository.findById(anyLong())).thenReturn(Optional.of(order));
        when(discoveryClient.getNextServerFromEureka("SERVICE-USER", false)).thenReturn(instanceInfo);
        when(instanceInfo.getHomePageUrl()).thenReturn("http://localhost:8080");
        when(restClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SimpleUserInfoResponse.class))
                .thenReturn(new SimpleUserInfoResponse("John", "123456789"));
        when(orderMapper.map(any(Order.class))).thenReturn(new OrderDto());

        OrderDto result = orderService.findOrderById(1L);

        assertNotNull(result);
        assertEquals("John", result.getUserName());
        assertEquals("123456789", result.getUserPhone());
        verify(orderViewStatsService, times(1)).recordOrderView(anyLong());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Должен выбрасывать исключение, если заказ не найден по Id")
    void shouldFindOrderById_Negative() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findOrderById(1L));
        verify(orderViewStatsService, never()).recordOrderView(anyLong());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Должен выбрасывать исключение, если запрос к сервису пользователя не удался")
    void shouldFindOrderById_RestClientException() {
        Order order = new Order();
        order.setUserId(1L);
        order.setPhoneId(1L);
        order.setTitle("Test Order");

        when(repository.findById(anyLong())).thenReturn(Optional.of(order));
        RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(repository.findById(anyLong())).thenReturn(Optional.of(order));
        when(discoveryClient.getNextServerFromEureka("SERVICE-USER", false)).thenReturn(instanceInfo);
        when(instanceInfo.getHomePageUrl()).thenReturn("http://localhost:8080");
        when(restClient.method(HttpMethod.GET)).thenThrow(new RestClientException("Service Unavailable"));

        assertThrows(RestClientException.class, () -> orderService.findOrderById(1L));
        verify(orderViewStatsService, never()).recordOrderView(anyLong());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("Должен успешно создавать новый заказ")
    void shouldCreateOrder_Positive() {
        NewOrderRequest request = new NewOrderRequest();
        request.setUserId(1L);
        request.setPhoneId(1L);
        request.setUserAddress("Test Address");
        request.setUserLocation(new Float[] {22.22F, 33.33F});
        request.setOrderCategory("TUTORING");
        request.setOrderName("Test Order");
        request.setOrderDescription("Test Description");
        request.setOrderAmount(100);
        request.setFinishOrderDate(LocalDate.from(LocalDateTime.now().plusDays(1)));

        orderService.createOrder(request);

        verify(repository, times(1)).save(any(Order.class));
        verify(orderViewStatsService, never()).recordOrderView(anyLong());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("Должен выбрасывать исключение при создании заказа с некорректной категорией")
    void shouldCreateOrder_InvalidCategory() {
        NewOrderRequest request = new NewOrderRequest();
        request.setUserId(1L);
        request.setPhoneId(1L);
        request.setUserAddress("Test Address");
        request.setUserLocation(new Float[] {22.22F, 33.33F});
        request.setOrderCategory("INVALID_CATEGORY");
        request.setOrderName("Test Order");
        request.setOrderDescription("Test Description");
        request.setOrderAmount(100);
        request.setFinishOrderDate(LocalDate.from(LocalDateTime.now().plusDays(1)));

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
        verify(repository, never()).save(any(Order.class));
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    @DisplayName("Должен успешно удалять заказ по его Id")
    void shouldDeleteOrder_Positive() {
        doNothing().when(repository).deleteById(anyLong());

        orderService.deleteOrder(1L);

        verify(repository, times(1)).deleteById(anyLong());
        verify(orderViewStatsService, times(1)).recordOrderView(anyLong());
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    @DisplayName("Должен выбрасывать исключение при ошибке удаления заказа по Id")
    void shouldDeleteOrder_Negative() {
        doThrow(new RuntimeException("Error")).when(repository).deleteById(anyLong());

        assertThrows(RuntimeException.class, () -> orderService.deleteOrder(1L));
        verify(orderViewStatsService, never()).recordOrderView(anyLong());
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    @DisplayName("Должен успешно обновлять заказ")
    void shouldUpdateOrder_Positive() {
        Order order = new Order();
        order.setId(1L);
        order.setTitle("Updated Order");

        orderService.updateOrder(order);

        verify(repository, times(1)).save(order);
        verify(orderViewStatsService, never()).recordOrderView(anyLong());
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    @DisplayName("Должен находить заказы по Id пользователя")
    void shouldFindOrdersByUserId_Positive() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(repository.findOrdersByUserId(anyLong())).thenReturn(orders);

        List<Order> result = orderService.findOrdersByUserId(1L);

        assertEquals(2, result.size());
        verify(repository, times(1)).findOrdersByUserId(anyLong());
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    @DisplayName("Должен возвращать пустой список, если заказы по Id пользователя не найдены")
    void shouldFindOrdersByUserId_Empty() {
        when(repository.findOrdersByUserId(anyLong())).thenReturn(Collections.emptyList());

        List<Order> result = orderService.findOrdersByUserId(1L);

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findOrdersByUserId(anyLong());
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    @DisplayName("Должен находить заказы по категориям")
    void shouldFindOrdersByCategories_Positive() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(repository.findOrdersByCategories(anyCollection())).thenReturn(orders);

        List<Order> result = orderService.findOrdersByCategories(1L);

        assertEquals(2, result.size());
        verify(repository, times(1)).findOrdersByCategories(anyCollection());
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    @DisplayName("Должен возвращать пустой список, если заказы по категориям не найдены")
    void shouldFindOrdersByCategories_Empty() {
        when(repository.findOrdersByCategories(anyCollection())).thenReturn(Collections.emptyList());

        List<Order> result = orderService.findOrdersByCategories(1L);

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findOrdersByCategories(anyCollection());
    }
}