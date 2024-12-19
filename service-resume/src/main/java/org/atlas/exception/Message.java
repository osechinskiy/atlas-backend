package org.atlas.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Message {

    INVALID_TYPE_OF_PERFORMED("Неверный тип выполняемых работ"),
    RESUME_NOT_FOUND("Резюме не найдено"),
    TYPE_OF_PERFORMED_NOT_FOUND("Не найдена информация о выполняемых работах");

    private final String name;
}
