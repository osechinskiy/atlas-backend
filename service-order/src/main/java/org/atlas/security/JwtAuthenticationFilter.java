package org.atlas.security;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.atlas.rest.dto.JwtValidateResponse;
import org.atlas.rest.exception.RestTemplateResponseErrorHandler;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String HEADER_NAME = "Authorization";

    private final EurekaClient discoveryClient;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Получаем токен из заголовка
        var authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Обрезаем префикс и получаем токен
        var jwt = authHeader.substring(BEARER_PREFIX.length());

        // Отправляем запрос на микросервис авторизации для проверки токена
        JwtValidateResponse validateResponse = getValidateInfo(jwt);
        if (validateResponse != null && validateResponse.isValid()) {
            // Если токен валиден, то аутентифицируем пользователя
            authenticateUser(validateResponse, request);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(JwtValidateResponse response, HttpServletRequest request) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Создаем объект аутентификации
        String username = response.getSubject();
        Long userId = response.getUserId();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                null
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }

    private JwtValidateResponse getValidateInfo(String token) {
        InstanceInfo clientInfo = discoveryClient.getNextServerFromEureka("SERVICE-AUTHORIZATION", false);
        RestClient restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(clientInfo.getHomePageUrl())
                .defaultStatusHandler(new RestTemplateResponseErrorHandler())
                .build();
        log.info("Rest client Request for UserDetails");
        return restClient.get()
                .uri("/validate-token?token=" + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(JwtValidateResponse.class);
    }
}
