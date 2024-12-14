package org.atlas.service;

import org.atlas.rest.dto.JwtAuthenticationResponse;
import org.atlas.rest.dto.JwtValidateResponse;
import org.atlas.rest.dto.SignInRequest;
import org.atlas.rest.dto.SignUpRequest;

public interface AuthenticationService {

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    JwtAuthenticationResponse signUp(SignUpRequest request);

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
     JwtAuthenticationResponse signIn(SignInRequest request);

    JwtValidateResponse validateToken(String token);
}
