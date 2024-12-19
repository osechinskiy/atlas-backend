package org.atlas.service.impl;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.atlas.rest.dto.JwtAuthenticationResponse;
import org.atlas.rest.dto.JwtValidateResponse;
import org.atlas.rest.dto.SignInRequest;
import org.atlas.rest.dto.SignUpRequest;
import org.atlas.rest.dto.AuthInfo;
import org.atlas.service.AuthenticationService;
import org.atlas.service.JwtService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;

    private final EurekaClient discoveryClient;

    private final PasswordEncoder passwordEncoder;

    private final RestClient restClient;


    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        AuthInfo userResponse = createUser(request);
        var jwt = jwtService.generateToken(userResponse);

        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        AuthInfo user = getUserInfo(request);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public JwtValidateResponse validateToken(String token) {
        if (StringUtils.isNotEmpty(token) && jwtService.isTokenValid(token)) {
            String subj = jwtService.extractUserEmail(token);
            Long userId = jwtService.extractUserId(token);
            return JwtValidateResponse.builder()
                    .subject(subj)
                    .userId(userId)
                    .isValid(true)
                    .build();
        }
        return null; // Возвращаем null или выбрасываем исключение, если токен недействителен
    }

    private String getHomeUrl() {
        return discoveryClient.getNextServerFromEureka("SERVICE-USER", false).getHomePageUrl();
    }

    private AuthInfo createUser(SignUpRequest request) {
        return restClient.post()
                .uri(getHomeUrl() + "/account")
                .accept(MediaType.APPLICATION_JSON)
                .body(request)// Передаем userDto в теле запроса
                .retrieve()
                .body(AuthInfo.class);
    }

    private AuthInfo getUserInfo(SignInRequest request) {
        return restClient.method(HttpMethod.GET)
                .uri(getHomeUrl() + "/account")
                .accept(MediaType.APPLICATION_JSON)
                .body(request)// Передаем userDto в теле запроса
                .retrieve()
                .body(AuthInfo.class);
    }
}
