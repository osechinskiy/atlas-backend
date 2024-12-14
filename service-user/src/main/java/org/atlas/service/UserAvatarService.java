package org.atlas.service;

import org.atlas.model.UserAvatar;

public interface UserAvatarService {

    /**
     * Установка аватара пользователя.
     * Метод позволяет установить или обновить аватар для указанного пользователя.
     *
     * @param userId уникальный идентификатор пользователя, для которого устанавливается аватар.
     * @param avatar массив байтов, представляющий изображение аватара.
     */
    void setUserAvatar(long userId, byte[] avatar);

    /**
     * Получение аватара пользователя по его идентификатору.
     * Метод возвращает аватар пользователя, связанный с указанным идентификатором.
     *
     * @param userId уникальный идентификатор пользователя, чей аватар необходимо получить.
     * @return объект UserAvatar, содержащий информацию об аватаре пользователя.
     */
    UserAvatar findByUserId(Long userId);
}