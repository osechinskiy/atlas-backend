package org.atlas.rest.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.atlas.rest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() ||
                response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // Обрабатываем ошибки на основе кода состояния
        HttpStatusCode statusCode = response.getStatusCode();
        // Чтение тела ответа
        String body = readBody(response.getBody());
        // Десериализация JSON в объект ErrorResponse
        ErrorResponse errorResponse = new ObjectMapper().readValue(body, ErrorResponse.class);

        if (statusCode.is4xxClientError()) {
            // Обработка ошибок клиента (4xx)
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                throw new UserUnAuthorized(errorResponse.getMessage());
            }
            if (statusCode == HttpStatus.NOT_FOUND) {
                throw new InformationNotFoundException(errorResponse.getMessage());
            }

            throw new HttpClientErrorException(statusCode, "Ошибка клиента: ");
        } else if (statusCode.is5xxServerError()) {
            // Обработка ошибок сервера (5xx)
            throw new HttpServerErrorException(statusCode, "Ошибка сервера: ");
        }
    }

    private String readBody(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
