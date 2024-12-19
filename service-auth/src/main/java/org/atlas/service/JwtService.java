package org.atlas.service;

import org.atlas.rest.dto.AuthInfo;

public interface JwtService {


    /**
     * Извлечение email пользователя из токена
     *
     * @param token токен
     * @return email пользователя
     */
    String extractUserEmail(String token);

    /**
     * Извлечение ID пользователя
     *
     * @param token токен
     * @return ID пользователя
     */
    Long extractUserId(String token);

    /**
     * Генерация токена
     *
     * @param authInfo данные пользователя
     * @return токен
     */
    String generateToken(AuthInfo authInfo);

    /**
     * Проверка токена на валидность
     *
     * @param token токен
     * @return true, если токен валиден
     */
    boolean isTokenValid(String token);
}
