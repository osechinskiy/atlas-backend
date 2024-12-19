package org.atlas.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, представляющий упрощенную информацию о пользователе.
 * Содержит только основные данные: имя и номер телефона.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUserInfoResponse {

    /**
     * Имя пользователя.
     */
    private String firstName;

    /**
     * Номер телефона пользователя.
     */
    private String phoneNumber;

}
