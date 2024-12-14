package org.atlas.service;

import java.util.Collection;
import java.util.List;
import org.atlas.exception.ChangePasswordException;
import org.atlas.exception.UserAlreadyExistException;
import org.atlas.exception.UserNotFoundException;
import org.atlas.exception.UserPhoneNotFound;
import org.atlas.rest.dto.ChangePasswordRequest;
import org.atlas.rest.dto.SignInRequest;
import org.atlas.rest.dto.SignUpRequest;
import org.atlas.model.User;
import org.atlas.rest.dto.SimpleUserInfoRequest;
import org.atlas.rest.dto.SimpleUserInfoResponse;
import org.atlas.rest.dto.UserInfoResponse;
import org.atlas.rest.dto.UserMainInfoRequest;

public interface UserService {

    /**
     * Создание нового пользователя. Метод принимает данные для регистрации пользователя и создает нового пользователя в
     * системе.
     *
     * @param request объект, содержащий данные для регистрации нового пользователя.
     * @return созданный пользователь с присвоенным идентификатором.
     * @throws UserAlreadyExistException если возникла ошибка при создании пользователя (например, если пользователь с
     * таким email уже существует).
     */
    User create(SignUpRequest request);

    /**
     * Получение пользователя по email. Метод принимает объект запроса с email и возвращает соответствующего
     * пользователя.
     *
     * @param request объект, содержащий email для поиска пользователя.
     * @return пользователь, соответствующий переданному email.
     * @throws UserNotFoundException если пользователь с указанным email не найден.
     */
    User getByEmail(SignInRequest request);

    /**
     * Получение пользователя по уникальному идентификатору (id). Метод возвращает пользователя по его уникальному
     * идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     * @return пользователь с указанным идентификатором.
     * @throws UserNotFoundException если пользователь с указанным id не найден.
     */
    User getById(Long id);

    /**
     * Обновление основной информации о пользователе. Метод обновляет основные данные пользователя, такие как имя,
     * фамилия и email.
     *
     * @param request объект, содержащий обновленные данные пользователя.
     * @return обновленный пользователь.
     */
    User updateUserMainInfo(UserMainInfoRequest request);

    /**
     * Изменение пароля пользователя. Метод позволяет пользователю изменить свой пароль, проверив старый пароль и
     * установив новый.
     *
     * @param userId уникальный идентификатор пользователя, для которого необходимо изменить пароль.
     * @param request объект, содержащий старый и новый пароли.
     * @return обновленный пользователь с измененным паролем.
     * @throws ChangePasswordException если старый пароль неверен или возникла ошибка при изменении пароля.
     */
    User changePassword(Long userId, ChangePasswordRequest request);

    /**
     * Получение упрощенной информации о пользователе по уникальному идентификатору (id). Метод возвращает упрощенную
     * информацию о пользователе, такую как имя и номер телефона.
     *
     * @param request дто запрос информации о пользователе
     * @return объект SimpleUserInfoResponse {@link SimpleUserInfoResponse}, содержащий упрощенную информацию о
     * пользователе.
     * @throws UserNotFoundException если пользователь с указанным id не найден.
     * @throws UserPhoneNotFound если телефон с указанным id не найден
     */
    SimpleUserInfoResponse getSimpleUserInfoById(SimpleUserInfoRequest request);

    /**
     * Получение информации о пользователе по уникальному идентификатору (id)
     *
     * @param userId Id пользователя
     * @param phoneId Id телефона пользователя
     * @return объект UserNotFoundException {@link UserNotFoundException}, содержащий информацию о пользователе.
     * @throws UserNotFoundException если пользователь с указанным id не найден.
     * @throws UserPhoneNotFound если телефон с указанным id не найден
     */
    UserInfoResponse getUserInfoById(Long userId, Long phoneId);

    /**
     * Получить пользователей по их уникальному идентификатору (id)
     *
     * @param userIds коллекция с id пользователей
     * @return список с объектами {@link User}
     */
    List<User> getUserByIds(Collection<Long> userIds);


    /**
     * Обновление данных пользователя в кэше. Метод обновляет информацию о пользователе в кэше, чтобы гарантировать
     * актуальность данных.
     *
     * @param user объект пользователя с обновленной информацией.
     * @return обновленный пользователь.
     */
    User updateCash(User user);
}
