package org.atlas.util;

import org.atlas.rest.exception.RestTemplateResponseErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Фабрика для создания экземпляров {@link RestClient}. Этот класс отвечает за конфигурацию и создание клиента REST,
 * который будет использоваться для выполнения HTTP-запросов.
 */
@Component
public class RestClientFactory {

    /**
     * Создает и настраивает экземпляр {@link RestClient}.
     *
     * @return Настроенный экземпляр {@link RestClient}. Использует {@link HttpComponentsClientHttpRequestFactory} для
     * обработки HTTP-запросов и {@link RestTemplateResponseErrorHandler} для обработки ошибок, возникающих при
     * выполнении запросов.
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .defaultStatusHandler(new RestTemplateResponseErrorHandler())
                .build();
    }
}
