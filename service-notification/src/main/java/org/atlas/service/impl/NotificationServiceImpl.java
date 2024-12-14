package org.atlas.service.impl;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atlas.model.OrderInfo;
import org.atlas.service.NotificationService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    private final EurekaClient discoveryClient;

    private final RestClient restClient;

    @Override
    @KafkaListener(topics = "order-topic", containerFactory = "listenerContainerFactory")
    public void consume(OrderInfo orderInfo) {
        log.info("log:{}", orderInfo);
        List<Long> results = geUserIdByCategories(orderInfo.getCategory());
        // Логика для отправки уведомлений пользователям
        results.forEach(res -> {
            messagingTemplate.convertAndSend("/topic/notifications/" + res , orderInfo);
        });

    }

    private List<Long> geUserIdByCategories(String category) {
        InstanceInfo instanceInfo = discoveryClient.getNextServerFromEureka("SERVICE-RESUME", false);
        String url = UriComponentsBuilder.fromHttpUrl(instanceInfo.getHomePageUrl())
                .path("/api/v1/resume/info")
                .queryParam("category", category)
                .toUriString();
        return restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
