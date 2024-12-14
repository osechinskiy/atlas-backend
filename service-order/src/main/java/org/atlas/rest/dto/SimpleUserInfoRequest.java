package org.atlas.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, представляющий запрос на получение упрощенной информацию о пользователе.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUserInfoRequest {

    /**
     * Id пользователя.
     */
    @NotNull(message = "Id пользователя не может быть пустым")
    private Long userId;

    /**
     * Id телефона пользователя.
     */
    @NotNull(message = "Id номер телефона пользователя не может быть пустым")
    private Long phoneId;

}
