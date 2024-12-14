package org.atlas.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atlas.config.TestSecurityConfig;
import org.atlas.rest.dto.JwtAuthenticationResponse;
import org.atlas.rest.dto.JwtValidateResponse;
import org.atlas.rest.dto.SignInRequest;
import org.atlas.rest.dto.SignUpRequest;
import org.atlas.service.AuthenticationService;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DisplayName("Должен регистрировать пользователя и возвращать JWT токен")
    void shouldSignUpAndReturnJwtToken() throws Exception {
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("jwtToken");

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName("Иван");
        signUpRequest.setLastName("Иванов");
        signUpRequest.setEmail("ivan@example.com");
        signUpRequest.setPassword("password123");

        when(authenticationService.signUp(any(SignUpRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"jwtToken\"}"));
    }

    @Test
    @Order(2)
    @DisplayName("Должен регистрировать пользователя с другим именем и возвращать JWT токен")
    void shouldSignUpWithDifferentUserAndReturnJwtToken() throws Exception {
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("anotherJwtToken");

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName("Петр");
        signUpRequest.setLastName("Петров");
        signUpRequest.setEmail("petr@example.com");
        signUpRequest.setPassword("anotherpassword");

        when(authenticationService.signUp(any(SignUpRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"anotherJwtToken\"}"));
    }

    @Test
    @Order(3)
    @DisplayName("Должен возвращать ошибку при регистрации пользователя с пустым именем")
    void shouldReturnErrorWhenSignUpWithEmptyFirstName() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName("");
        signUpRequest.setLastName("Иванов");
        signUpRequest.setEmail("ivan@example.com");
        signUpRequest.setPassword("password123");

        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @DisplayName("Должен возвращать ошибку при регистрации пользователя с некорректным email")
    void shouldReturnErrorWhenSignUpWithInvalidEmail() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName("Иван");
        signUpRequest.setLastName("Иванов");
        signUpRequest.setEmail("invalid-email");
        signUpRequest.setPassword("password123");

        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("Должен аутентифицировать пользователя и возвращать JWT токен")
    void shouldSignInAndReturnJwtToken() throws Exception {
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("jwtToken");

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("testuser@example.com");
        signInRequest.setPassword("password");

        when(authenticationService.signIn(any(SignInRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"jwtToken\"}"));
    }

    @Test
    @Order(6)
    @DisplayName("Должен аутентифицировать другого пользователя и возвращать JWT токен")
    void shouldSignInWithDifferentUserAndReturnJwtToken() throws Exception {
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("anotherJwtToken");

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("anotheruser@example.com");
        signInRequest.setPassword("anotherpassword");

        when(authenticationService.signIn(any(SignInRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"anotherJwtToken\"}"));
    }

    @Test
    @Order(7)
    @DisplayName("Должен возвращать ошибку при аутентификации пользователя с пустым email")
    void shouldReturnErrorWhenSignInWithEmptyEmail() throws Exception {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("");
        signInRequest.setPassword("password");

        mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    @DisplayName("Должен возвращать ошибку при аутентификации пользователя с пустым паролем")
    void shouldReturnErrorWhenSignInWithEmptyPassword() throws Exception {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("testuser@example.com");
        signInRequest.setPassword("");

        mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    @DisplayName("Должен валидировать JWT токен и возвращать информацию о пользователе")
    void shouldValidateJwtTokenAndReturnUserInfo() throws Exception {
        JwtValidateResponse validateResponse = JwtValidateResponse.builder()
                .subject("testuser")
                .userId(1L)
                .isValid(true)
                .build();

        when(authenticationService.validateToken(anyString())).thenReturn(validateResponse);

        mockMvc.perform(get("/validate-token")
                        .param("token", "jwtToken"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(validateResponse)));
    }

    @Test
    @Order(10)
    @DisplayName("Должен валидировать другой JWT токен и возвращать информацию о пользователе")
    void shouldValidateAnotherJwtTokenAndReturnUserInfo() throws Exception {
        JwtValidateResponse validateResponse = JwtValidateResponse.builder()
                .subject("anotheruser")
                .userId(2L)
                .isValid(true)
                .build();

        when(authenticationService.validateToken(anyString())).thenReturn(validateResponse);

        mockMvc.perform(get("/validate-token")
                        .param("token", "anotherJwtToken"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(validateResponse)));
    }


    @Test
    @Order(11)
    @DisplayName("Должен возвращать ошибку при валидации недействительного JWT токена")
    void shouldReturnErrorWhenValidateInvalidJwtToken() throws Exception {
        when(authenticationService.validateToken(anyString())).thenReturn(null);

        mockMvc.perform(get("/validate-token")
                        .param("token", "invalidJwtToken"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}