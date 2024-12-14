package org.atlas.rest.dto;

import org.springframework.http.HttpStatusCode;

/**
 * Класс, представляющий ответ об ошибке в формате JSON. Используется для структурированной обработки ошибок в
 * приложении.
 *
 * @param status Код состояния HTTP, связанный с ошибкой.
 * @param message Сообщение об ошибке, которое будет возвращено клиенту
 */

public record CustomErrorResponse(HttpStatusCode status, String message) {

}
