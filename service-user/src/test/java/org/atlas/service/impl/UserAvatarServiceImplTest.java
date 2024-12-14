package org.atlas.service.impl;

import org.atlas.model.User;
import org.atlas.model.UserAvatar;
import org.atlas.repository.UserAvatarRepository;
import org.atlas.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Base64;
import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserAvatarServiceImplTest {

    @Mock
    private UserAvatarRepository repository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserAvatarServiceImpl userAvatarService;


    @Test
    @Order(1)
    @DisplayName("Должен установить новый аватар для пользователя без существующего аватара")
    void shouldSetNewAvatarForUserWithoutExistingAvatar() {
        long userId = 1L;
        byte[] avatar = "avatar".getBytes();
        String base64Encoded = Base64.getEncoder().encodeToString(avatar);

        User user = new User();
        user.setId(userId);

        when(userService.getById(userId)).thenReturn(user);
        when(repository.save(any(UserAvatar.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userAvatarService.setUserAvatar(userId, avatar);

        assertNotNull(user.getAvatar());
        assertEquals(base64Encoded, user.getAvatar().getAvatar());
        verify(userService).updateCash(user);
    }

    @Test
    @Order(2)
    @DisplayName("Должен обновить аватар для пользователя с существующим аватаром")
    void shouldUpdateAvatarForUserWithExistingAvatar() {
        long userId = 1L;
        byte[] avatar = "newAvatar".getBytes();
        String base64Encoded = Base64.getEncoder().encodeToString(avatar);

        User user = new User();
        user.setId(userId);
        UserAvatar existingAvatar = new UserAvatar();
        existingAvatar.setAvatar("oldAvatar");
        user.setAvatar(existingAvatar);

        when(userService.getById(userId)).thenReturn(user);
        when(repository.save(any(UserAvatar.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userAvatarService.setUserAvatar(userId, avatar);

        assertNotNull(user.getAvatar());
        assertEquals(base64Encoded, user.getAvatar().getAvatar());
        verify(userService).updateCash(user);
    }

    @Test
    @Order(3)
    @DisplayName("Должен найти аватар пользователя по идентификатору пользователя, если аватар найден")
    void shouldFindUserAvatarByUserIdWhenAvatarIsFound() {
        long userId = 1L;
        UserAvatar userAvatar = new UserAvatar();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(userAvatar));

        UserAvatar result = userAvatarService.findByUserId(userId);

        assertNotNull(result);
        assertEquals(userAvatar, result);
    }

    @Test
    @Order(4)
    @DisplayName("Должен вернуть null, если аватар пользователя по идентификатору пользователя не найден")
    void shouldReturnNullIfUserAvatarByUserIdIsNotFound() {
        long userId = 1L;

        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        UserAvatar result = userAvatarService.findByUserId(userId);

        assertNull(result);
    }
}