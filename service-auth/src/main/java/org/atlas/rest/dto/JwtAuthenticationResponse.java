package org.atlas.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Ответ c токеном доступа
 */
@Data
@AllArgsConstructor
public class JwtAuthenticationResponse {

    private String token;
}
