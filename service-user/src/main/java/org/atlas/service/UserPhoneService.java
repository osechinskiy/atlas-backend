package org.atlas.service;

import org.atlas.rest.dto.CreateUserPhoneRequest;
import org.atlas.rest.dto.UpdateUserPhoneRequest;

public interface UserPhoneService {

    /**
     * Удаление номера телефона пользователя по его идентификатору.
     * Метод удаляет номер телефона, связанный с указанным пользователем, по его уникальному идентификатору.
     *
     * @param userId уникальный идентификатор пользователя, чьи номера телефонов нужно обновить.
     * @param phoneId уникальный идентификатор номера телефона, который необходимо удалить.
     */
    void deleteByPhoneId(long userId, long phoneId);

    /**
     * Обновление информации о номере телефона.
     * Метод обновляет данные существующего номера телефона, включая его описание и статус.
     *
     * @param request объект, содержащий данные для обновления номера телефона.
     */
    void updatePhoneNumber(UpdateUserPhoneRequest request);

    /**
     * Создание нового номера телефона для пользователя.
     * Метод добавляет новый номер телефона к указанному пользователю.
     *
     * @param request объект, содержащий данные для создания нового номера телефона.
     */
    void createPhoneNumber(CreateUserPhoneRequest request);

}
