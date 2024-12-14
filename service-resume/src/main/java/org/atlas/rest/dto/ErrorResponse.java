package org.atlas.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, представляющий ответ об ошибке в формате JSON. Используется для структурированной обработки ошибок в
 * приложении.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    /**
     * Статус ошибки
     */
    private String status;

    /**
     * Сообщение об ошибке
     */
    private String message;
}
