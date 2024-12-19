package org.atlas.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Message {

    USER_BY_USERNAME_NOT_FOUND("Пользователь %s не найден"),
    USER_BY_EMAIL_NOT_FOUND("Пользователь, с Email %s, не найден"),
    USER_ALREADY_EXIST("Пользователь с именем %s уже существует"),
    USER_EMAIL_ALREADY_EXIST("Пользователь с email %s уже существует"),
    INCORRECT_OLD_PASSWORD("Указан неверный текущий пароль"),
    INVALID_PASSWORD("Неверный пароль"),
    PHONE_NOT_FOUND("Номер телефона не найден");

    private final String title;
}
