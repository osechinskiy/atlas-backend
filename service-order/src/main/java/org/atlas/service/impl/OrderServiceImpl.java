package org.atlas.service.impl;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.atlas.exception.OrderNotFoundException;
import org.atlas.mapper.OrderMapper;
import org.atlas.model.Order;
import org.atlas.model.OrderTypes;
import org.atlas.repository.OrderRepository;
import org.atlas.rest.dto.NewOrderRequest;
import org.atlas.model.dto.OrderDto;
import org.atlas.rest.dto.SimpleUserInfoRequest;
import org.atlas.rest.dto.SimpleUserInfoResponse;
import org.atlas.service.DataSender;
import org.atlas.service.OrderService;
import org.atlas.service.OrderViewStatsService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final String MESSAGE = "Заказ не найден";

    private final OrderRepository repository;

    private final OrderViewStatsService orderViewStatsService;

    private final EurekaClient discoveryClient;

    private final RestClient restClient;

    private final OrderMapper orderMapper;

    private final DataSender dataSender;


    @Override
    @Transactional
    public List<Order> findOrdersByUserId(Long userId) {
        return repository.findOrdersByUserId(userId);
    }

    @Override
    @Transactional
    public List<Order> findOrdersByCategories(Long userId) {
        List<String> resumeCategory = geUserResumeCategoriesInfo(userId);

        return repository.findOrdersByCategories(OrderTypes.getOrderTypesByString(resumeCategory));
    }

    @Override
    @Transactional
    public void createOrder(NewOrderRequest request) {
        Order order = repository.save(Order.builder()
                .userId(request.getUserId())
                .phoneId(request.getPhoneId())
                .address(request.getUserAddress())
                .location(StringUtils.join(request.getUserLocation(), ","))
                .category(OrderTypes.valueOf(request.getOrderCategory()))
                .title(request.getOrderName())
                .description(request.getOrderDescription())
                .amount(request.getOrderAmount())
                .creationTimestamp(LocalDateTime.now())
                .desiredCompletionDate(request.getFinishOrderDate())
                .build());
        log.info("New order created: {}", request);
        dataSender.send(orderMapper.mapOrderInfo(order));
    }

    @Override
    @Transactional
    public OrderDto findOrderById(Long orderId) {
        Order order = repository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(MESSAGE));
        SimpleUserInfoResponse response = getSimpleUserInfo(
                new SimpleUserInfoRequest(order.getUserId(), order.getPhoneId()));

        OrderDto orderDto = orderMapper.map(order);
        orderDto.setUserName(response.getFirstName());
        orderDto.setUserPhone(response.getPhoneNumber());

        orderViewStatsService.recordOrderView(orderId);

        return orderDto;
    }

    @Override
    @Transactional
    public void updateOrder(Order order) {
        log.info("Update order: {}", order);
        repository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        repository.deleteById(orderId);
        log.info("Order deleted id_order: {}", orderId);
        orderViewStatsService.deleteByOrderId(orderId);
        log.info("Order stats deleted id_order: {}", orderId);
    }

    /**
     * Получает информацию о пользователе в упрощенном формате. Этот метод извлекает информацию о пользователе,
     * отправляя запрос к сервису пользователя, используя его URL, полученный из Eureka.
     *
     * @param request Объект запроса, содержащий информацию о пользователе, включая идентификатор пользователя и
     * идентификатор телефона.
     * @return Объект SimpleUserInfoResponse, содержащий информацию о пользователе.
     * @throws RestClientException Если произошла ошибка при выполнении запроса к сервису.
     */
    private SimpleUserInfoResponse getSimpleUserInfo(SimpleUserInfoRequest request) {
        InstanceInfo instanceInfo = discoveryClient.getNextServerFromEureka("SERVICE-USER", false);
        String url = UriComponentsBuilder.fromHttpUrl(instanceInfo.getHomePageUrl())
                .path("/api/v1/user/info")
                .queryParam("userId", request.getUserId())
                .queryParam("phoneId", request.getPhoneId())
                .toUriString();
        return restClient.method(HttpMethod.GET)
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(SimpleUserInfoResponse.class);
    }

    private List<String> geUserResumeCategoriesInfo(Long userId) {
        InstanceInfo instanceInfo = discoveryClient.getNextServerFromEureka("SERVICE-RESUME", false);
        String url = UriComponentsBuilder.fromHttpUrl(instanceInfo.getHomePageUrl())
                .path("/api/v1/resume/categories_info")
                .queryParam("userId", userId)
                .toUriString();
        return restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {
                });
    }
}
