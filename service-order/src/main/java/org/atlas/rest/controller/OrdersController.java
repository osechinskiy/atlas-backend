package org.atlas.rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.atlas.mapper.OrderMapper;
import org.atlas.model.OrderViewStats;
import org.atlas.rest.dto.NewOrderRequest;
import org.atlas.model.dto.OrderDto;
import org.atlas.security.UserService;
import org.atlas.service.OrderService;
import org.atlas.service.OrderViewStatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    private final OrderViewStatsService orderViewStatsService;

    private final UserService userService;

    private final OrderMapper mapper;

    @GetMapping("/user/{id}")
    public ResponseEntity<List<OrderDto>> getOrders(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.mapOrders(orderService.findOrdersByUserId(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDto>> getAllOrders(@RequestParam (value = "userId") Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.mapOrders(orderService.findOrdersByCategories(userId)));
    }

    @PostMapping("/api/v1/order")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(@RequestBody @Valid NewOrderRequest request) {
        orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable("id") Long id) {
        OrderDto orderDto = orderService.findOrderById(id);
        orderDto.setEditable(userService.canEdit(orderDto.getUserId()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrder(@PathVariable("id") Long orderId, @RequestBody @Valid OrderDto orderDto) {
        orderService.updateOrder(mapper.map(orderDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("id") Long orderId) {
        orderService.deleteOrder(orderId);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<List<OrderViewStats>> getViewStats(@PathVariable Long id) {
        List<OrderViewStats> stats = orderViewStatsService.getOrderViewStats(id);
        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }
}