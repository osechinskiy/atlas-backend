package org.atlas.service.impl;

import java.util.HashSet;
import java.util.Set;
import org.atlas.model.User;
import org.atlas.model.UserPhoneNumber;
import org.atlas.repository.UserPhoneRepository;
import org.atlas.rest.dto.CreateUserPhoneRequest;
import org.atlas.rest.dto.UpdateUserPhoneRequest;
import org.atlas.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserPhoneServiceImplTest {

    @Mock
    private UserPhoneRepository repository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserPhoneServiceImpl userPhoneService;

    @Test
    @Order(1)
    @DisplayName("Должен успешно удалить номер телефона по ID")
    void shouldDeletePhoneNumberById() {
        User user = new User();
        UserPhoneNumber phoneNumber = new UserPhoneNumber();
        phoneNumber.setId(1L);
        Set<UserPhoneNumber> phoneNumbers = new HashSet<>();
        phoneNumbers.add(phoneNumber);
        user.setPhoneNumbers(phoneNumbers);

        when(userService.getById(1L)).thenReturn(user);

        userPhoneService.deleteByPhoneId(1L, 1L);

        verify(repository).deleteById(1L);
        verify(userService).updateCash(user);
        assertEquals(0, user.getPhoneNumbers().size());
    }

    @Test
    @Order(2)
    @DisplayName("Должен успешно обновить номер телефона")
    void shouldUpdatePhoneNumber() {
        UserPhoneNumber phoneNumber = new UserPhoneNumber();
        phoneNumber.setId(1L);
        phoneNumber.setUserId(1L);
        phoneNumber.setPhone("123456789");

        User user = new User();
        user.setPhoneNumbers(Collections.singleton(phoneNumber));

        UpdateUserPhoneRequest request = new UpdateUserPhoneRequest();
        request.setId(1L);
        request.setPhone("987654321");

        when(repository.findById(1L)).thenReturn(Optional.of(phoneNumber));
        when(userService.getById(1L)).thenReturn(user);

        userPhoneService.updatePhoneNumber(request);

        verify(repository).save(phoneNumber);
        verify(userService).updateCash(user);
        assertEquals("987654321", phoneNumber.getPhone());
    }

    @Test
    @Order(3)
    @DisplayName("Должен успешно создать номер телефона")
    void shouldCreatePhoneNumber() {
        User user = new User();
        user.setId(1L);
        user.setPhoneNumbers(new HashSet<>());

        CreateUserPhoneRequest request = new CreateUserPhoneRequest();
        request.setUserId(1L);
        request.setPhone("123456789");

        when(userService.getById(1L)).thenReturn(user);
        when(repository.save(any(UserPhoneNumber.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userPhoneService.createPhoneNumber(request);

        ArgumentCaptor<UserPhoneNumber> captor = ArgumentCaptor.forClass(UserPhoneNumber.class);
        verify(repository).save(captor.capture());
        verify(userService).updateCash(user);

        UserPhoneNumber savedPhoneNumber = captor.getValue();
        assertEquals(1L, savedPhoneNumber.getUserId());
        assertEquals("123456789", savedPhoneNumber.getPhone());
        assertEquals(1, user.getPhoneNumbers().size());
        assertEquals(savedPhoneNumber, user.getPhoneNumbers().iterator().next());
    }
}