package org.atlas.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.netflix.discovery.EurekaClient;
import java.time.LocalDate;
import org.atlas.config.TestSecurityConfig;
import org.atlas.mapper.OrderMapper;
import org.atlas.model.OrderViewStats;
import org.atlas.rest.dto.NewOrderRequest;
import org.atlas.model.dto.OrderDto;
import org.atlas.security.UserService;
import org.atlas.service.OrderService;
import org.atlas.service.OrderViewStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdersController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private EurekaClient eurekaClient;

    @MockBean
    private OrderViewStatsService orderViewStatsService;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderMapper mapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    @Order(1)
    @WithMockUser
    @DisplayName("Должен возвращать заказы пользователя по его ID")
    void getOrders_Positive() throws Exception {
        Long userId = 1L;
        List<OrderDto> orderDtos = List.of(new OrderDto());

        when(orderService.findOrdersByUserId(userId)).thenReturn(List.of());
        when(mapper.mapOrders(any())).thenReturn(orderDtos);

        mockMvc.perform(get("/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(2)
    @WithMockUser
    @DisplayName("Должен возвращать все заказы")
    void getAllOrders_Positive() throws Exception {
        List<OrderDto> orderDtos = List.of(new OrderDto());

        when(orderService.findOrdersByCategories(anyLong())).thenReturn(List.of());
        when(mapper.mapOrders(anyList())).thenReturn(orderDtos);

        mockMvc.perform(get("/all")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(3)
    @WithMockUser
    @DisplayName("Должен создавать новый заказ")
    void createOrder_Positive() throws Exception {
        NewOrderRequest request = new NewOrderRequest(
                1L,
                1234567890L,
                "123 Main St, Apt 4B",
                new Float[]{40.7128f, -74.0060f},
                "Electronics",
                "New Smartphone",
                "I need a new smartphone with the latest features.",
                LocalDate.now().plusDays(10),
                1
        );

        doNothing().when(orderService).createOrder(any(NewOrderRequest.class));

        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(orderService, times(1)).createOrder(any(NewOrderRequest.class));
    }

    @Test
    @Order(4)
    @WithMockUser
    @DisplayName("Должен возвращать заказ по его ID")
    void getOrder_Positive() throws Exception {
        Long orderId = 1L;
        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(1L);

        when(orderService.findOrderById(orderId)).thenReturn(orderDto);
        when(userService.canEdit(orderDto.getUserId())).thenReturn(true);

        mockMvc.perform(get("/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.editable").value(true));
    }

    @Test
    @Order(5)
    @WithMockUser
    @DisplayName("Должен обновлять заказ")
    void updateOrder_Positive() throws Exception {
        Long orderId = 1L;
        OrderDto orderDto = new OrderDto();

        doNothing().when(orderService).updateOrder(any());

        mockMvc.perform(patch("/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isAccepted());

        verify(orderService, times(1)).updateOrder(any());
    }

    @Test
    @Order(6)
    @WithMockUser
    @DisplayName("Должен удалять заказ по его ID")
    void deleteOrder_Positive() throws Exception {
        Long orderId = 1L;

        doNothing().when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(orderId);
    }

    @Test
    @Order(7)
    @WithMockUser
    @DisplayName("Должен возвращать статистику просмотров заказа по его ID")
    void getViewStats_Positive() throws Exception {
        Long orderId = 1L;
        List<OrderViewStats> stats = List.of(new OrderViewStats());

        when(orderViewStatsService.getOrderViewStats(orderId)).thenReturn(stats);

        mockMvc.perform(get("/{id}/stats", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}