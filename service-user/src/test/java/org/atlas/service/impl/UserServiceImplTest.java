package org.atlas.service.impl;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.atlas.exception.ChangePasswordException;
import org.atlas.exception.InvalidPasswordException;
import org.atlas.exception.UserAlreadyExistException;
import org.atlas.exception.UserNotFoundException;
import org.atlas.exception.UserPhoneNotFound;
import org.atlas.model.Role;
import org.atlas.model.User;
import org.atlas.model.UserPhoneNumber;
import org.atlas.repository.UserRepository;
import org.atlas.rest.dto.ChangePasswordRequest;
import org.atlas.rest.dto.SignInRequest;
import org.atlas.rest.dto.SignUpRequest;
import org.atlas.rest.dto.SimpleUserInfoRequest;
import org.atlas.rest.dto.SimpleUserInfoResponse;
import org.atlas.rest.dto.UserMainInfoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = spy(new UserServiceImpl(userRepository, passwordEncoder, userService));
    }

    @Test
    @Order(1)
    @DisplayName("Должен создать пользователя")
    void shouldCreateUserSuccessfully() {
        SignUpRequest request = new SignUpRequest("John", "Doe", "john.doe@example.com", "password");
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password("encodedPassword")
                .authority(Role.NO_CATEGORY)
                .isActive(true)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.create(request);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(request.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @Order(2)
    @DisplayName("Должен выбросить исключение, если пользователь уже существует")
    void shouldThrowExceptionWhenUserAlreadyExists() {
        SignUpRequest request = new SignUpRequest("John", "Doe", "john.doe@example.com", "password");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> userService.create(request));
    }

    @Test
    @Order(3)
    @DisplayName("Должен успешно получить пользователя по email")
    void shouldGetUserByEmailSuccessfully() {
        SignInRequest request = new SignInRequest("john.doe@example.com", "password");
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        User foundUser = userService.getByEmail(request);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    @Order(4)
    @DisplayName("Должен выбросить исключение, если пользователь не найден по email")
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        SignInRequest request = new SignInRequest("john.doe@example.com", "password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getByEmail(request));
    }

    @Test
    @Order(5)
    @DisplayName("Должен выбросить исключение, если пароль неверный")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        SignInRequest request = new SignInRequest("john.doe@example.com", "password");
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userService.getByEmail(request));
    }

    @Test
    @Order(6)
    @DisplayName("Должен получить пользователя по ID")
    void shouldGetUserByIdSuccessfully() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.getById(userId);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
    }

    @Test
    @Order(7)
    @DisplayName("Должен выбросить исключение, если пользователь не найден по ID")
    void shouldThrowExceptionWhenUserNotFoundById() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    @Order(8)
    @DisplayName("Должен обновить основную информацию о пользователе")
    void shouldUpdateUserMainInfoSuccessfully() {
        UserMainInfoRequest request = new UserMainInfoRequest(1L, "John", "Doe", "john.doe@example.com",
                LocalDate.parse("1990-01-01"), "MALE");
        User user = new User();
        user.setId(request.getId());

        when(userRepository.findById(request.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUserMainInfo(request);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstName()).isEqualTo(request.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @Order(9)
    @DisplayName("Должен изменить пароль")
    void shouldChangePasswordSuccessfully() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword");
        User user = new User();
        user.setId(userId);
        user.setPassword("encodedOldPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.changePassword(userId, request);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getPassword()).isEqualTo("encodedNewPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @Order(10)
    @DisplayName("Должен выбросить исключение, если старый пароль неверный")
    void shouldThrowExceptionWhenOldPasswordIsIncorrect() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword");
        User user = new User();
        user.setId(userId);
        user.setPassword("encodedOldPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(ChangePasswordException.class, () -> userService.changePassword(userId, request));
    }

    @Test
    @Order(11)
    @DisplayName("Должен обновить кеш пользователя")
    void shouldUpdateUserCashSuccessfully() {
        User user = new User();
        user.setId(1L);

        User updatedUser = userService.updateCash(user);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
    }

    @Test
    @Order(12)
    @DisplayName("Должен получить упрощенную информацию о пользователе по ID")
    void shouldGetSimpleUserInfoByIdSuccessfully() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setPhoneNumbers(Set.of(new UserPhoneNumber(1L, userId, "1234567890")));

        SimpleUserInfoRequest request = new SimpleUserInfoRequest(1L, 1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        SimpleUserInfoResponse simpleUserInfo = userService.getSimpleUserInfoById(request);

        assertAll("check simpleUserInfo",
                () -> assertNotNull(simpleUserInfo),
                () -> assertEquals("John", simpleUserInfo.getFirstName()),
                () -> assertEquals("1234567890", simpleUserInfo.getPhoneNumber())
        );
    }

    @Test
    @Order(13)
    @DisplayName("Должен выбросить исключение, если телефон пользователя не найден по ID")
    void shouldThrowExceptionWhenPhoneNotFoundById() {
        SimpleUserInfoRequest request = new SimpleUserInfoRequest(1L, 1L);
        User user = new User();
        user.setId(1L);
        user.setPhoneNumbers(Set.of(new UserPhoneNumber(3L, 1L, "1234567890")));

        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));

        assertThrows(UserPhoneNotFound.class, () -> userService.getSimpleUserInfoById(request));
    }
}