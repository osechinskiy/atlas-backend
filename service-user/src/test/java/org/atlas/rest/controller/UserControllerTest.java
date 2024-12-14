package org.atlas.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.netflix.discovery.EurekaClient;
import java.time.LocalDate;
import org.atlas.config.TestSecurityConfig;
import org.atlas.exception.UserNotFoundException;
import org.atlas.mapper.UserMapper;
import org.atlas.model.User;
import org.atlas.rest.dto.AuthInfo;
import org.atlas.rest.dto.ChangePasswordRequest;
import org.atlas.rest.dto.CreateUserPhoneRequest;
import org.atlas.rest.dto.SignInRequest;
import org.atlas.rest.dto.SignUpRequest;
import org.atlas.rest.dto.SimpleUserInfoRequest;
import org.atlas.rest.dto.SimpleUserInfoResponse;
import org.atlas.rest.dto.UpdateUserPhoneRequest;
import org.atlas.model.dto.UserDto;
import org.atlas.rest.dto.UserMainInfoRequest;
import org.atlas.service.UserAvatarService;
import org.atlas.service.UserPhoneService;
import org.atlas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserAvatarService userAvatarService;

    @MockBean
    private UserPhoneService userPhoneService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private EurekaClient eurekaClient;

    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Order(1)
    @WithMockUser
    @DisplayName("Должен успешно получить пользователя по email")
    void shouldGetUserByEmailSuccessfully() throws Exception {
        SignInRequest request = new SignInRequest("john.doe@example.com", "password");
        AuthInfo authInfo = new AuthInfo();
        authInfo.setEmail(request.getEmail());

        when(userService.getByEmail(any(SignInRequest.class))).thenReturn(new User());
        when(userMapper.mapToAuthInfo(any(User.class))).thenReturn(authInfo);

        mockMvc.perform(get("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(request.getEmail()));
    }

    @Test
    @Order(2)
    @WithMockUser
    @DisplayName("Должен успешно зарегистрировать пользователя")
    void shouldSignUpSuccessfully() throws Exception {
        SignUpRequest request = new SignUpRequest("John", "Doe", "john.doe@example.com", "password");
        AuthInfo authInfo = new AuthInfo();
        authInfo.setEmail(request.getEmail());

        when(userService.create(any(SignUpRequest.class))).thenReturn(new User());
        when(userMapper.mapToAuthInfo(any(User.class))).thenReturn(authInfo);

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(request.getEmail()));
    }

    @Test
    @WithMockUser(username = "user")
    @Order(3)
    @DisplayName("Должен успешно получить пользователя по ID")
    void shouldGetUserByIdSuccessfully() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);

        when(userService.getById(userId)).thenReturn(new User());
        when(userMapper.map(any(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    @WithMockUser(username = "user")
    @Order(4)
    @DisplayName("Должен успешно обновить основную информацию о пользователе")
    void shouldUpdateUserMainInfoSuccessfully() throws Exception {
        UserMainInfoRequest request = new UserMainInfoRequest(1L, "John", "Doe",
                "john.doe@example.com", LocalDate.parse("1990-01-01"), "MALE");

        mockMvc.perform(patch("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(userService).updateUserMainInfo(any(UserMainInfoRequest.class));
    }

    @Test
    @WithMockUser(username = "user")
    @Order(5)
    @DisplayName("Должен успешно загрузить аватар")
    void shouldUploadAvatarSuccessfully() throws Exception {
        long userId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "some-image-data".getBytes());

        mockMvc.perform(multipart("/upload-avatar")
                        .file(file)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().string("Аватар успешно загружен."));

        verify(userAvatarService).setUserAvatar(eq(userId), any(byte[].class));
    }

    @Test
    @WithMockUser(username = "user")
    @Order(6)
    @DisplayName("Должен вернуть ошибку при загрузке пустого файла")
    void shouldReturnErrorWhenUploadingEmptyFile() throws Exception {
        long userId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "", "image/png", new byte[0]);

        mockMvc.perform(multipart("/upload-avatar")
                        .file(file)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Нет файла для загрузки."));
    }

    @Test
    @WithMockUser(username = "user")
    @Order(7)
    @DisplayName("Должен успешно удалить телефон")
    void shouldDeletePhoneSuccessfully() throws Exception {
        long userId = 1L;
        long phoneId = 1L;

        mockMvc.perform(delete("/" + userId + "/phone/" + phoneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userPhoneService).deleteByPhoneId(userId, phoneId);
    }

    @Test
    @WithMockUser(username = "user")
    @Order(8)
    @DisplayName("Должен успешно обновить номер телефона")
    void shouldUpdatePhoneSuccessfully() throws Exception {
        UpdateUserPhoneRequest request = new UpdateUserPhoneRequest(1L, 1L, "89099099090");

        mockMvc.perform(patch("/phones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(userPhoneService).updatePhoneNumber(any(UpdateUserPhoneRequest.class));
    }

    @Test
    @WithMockUser(username = "user")
    @Order(9)
    @DisplayName("Должен успешно создать номер телефона")
    void shouldCreatePhoneSuccessfully() throws Exception {
        CreateUserPhoneRequest request = new CreateUserPhoneRequest(1L, "89099099099");

        mockMvc.perform(post("/phones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userPhoneService).createPhoneNumber(any(CreateUserPhoneRequest.class));
    }

    @Test
    @WithMockUser(username = "user")
    @Order(10)
    @DisplayName("Должен успешно изменить пароль")
    void shouldChangePasswordSuccessfully() throws Exception {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword");

        mockMvc.perform(patch("/" + userId + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isAccepted());

        verify(userService).changePassword(eq(userId), any(ChangePasswordRequest.class));
    }

    @Test
    @WithMockUser(username = "user")
    @Order(11)
    @DisplayName("Должен успешно получить упрощенную информацию о пользователе по ID")
    void shouldGetSimpleUserInfoByIdSuccessfully() throws Exception {
        SimpleUserInfoResponse response = new SimpleUserInfoResponse("John", "1234567890");

        when(userService.getSimpleUserInfoById(any(SimpleUserInfoRequest.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/user/info")
                        .param("userId", "1")
                        .param("phoneId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.phoneNumber").value(response.getPhoneNumber()));
    }

    @Test
    @WithMockUser(username = "user")
    @Order(12)
    @DisplayName("Должен выбросить исключение, если пользователь не найден по ID")
    void shouldThrowExceptionWhenUserNotFoundById() throws Exception {
        SimpleUserInfoRequest request = new SimpleUserInfoRequest(1L, 1L);

        when(userService.getSimpleUserInfoById(any(SimpleUserInfoRequest.class))).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/user/info")
                        .param("userId", "1")
                        .param("phoneId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}