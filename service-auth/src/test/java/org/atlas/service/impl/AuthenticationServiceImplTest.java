package org.atlas.service.impl;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.atlas.rest.dto.AuthInfo;
import org.atlas.rest.dto.JwtAuthenticationResponse;
import org.atlas.rest.dto.JwtValidateResponse;
import org.atlas.rest.dto.SignInRequest;
import org.atlas.rest.dto.SignUpRequest;
import org.atlas.service.JwtService;
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
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private EurekaClient discoveryClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private InstanceInfo instanceInfo;

    @Mock
    private RestClient restClient;

    @Test
    @Order(1)
    @DisplayName("Должен регистрировать пользователя и генерировать JWT токен")
    void shouldSignUpUserAndGenerateJwtToken() {
        SignUpRequest signUpRequest = new SignUpRequest();
        AuthInfo authInfo = new AuthInfo();
        String jwtToken = "jwtToken";

        RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(discoveryClient.getNextServerFromEureka("SERVICE-USER", false)).thenReturn(instanceInfo);
        when(instanceInfo.getHomePageUrl()).thenReturn("http://localhost:8080");
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(SignUpRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(AuthInfo.class)).thenReturn(authInfo);
        when(jwtService.generateToken(authInfo)).thenReturn(jwtToken);

        JwtAuthenticationResponse response = authenticationService.signUp(signUpRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
    }


    @Test
    @Order(3)
    @DisplayName("Должен возвращать ошибку при регистрации пользователя с существующим email")
    void shouldReturnErrorWhenSignUpUserWithExistingEmail() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("existing@example.com");

        when(restClient.post())
                .thenThrow(new RuntimeException("User already exists"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.signUp(signUpRequest);
        });

        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Должен входить в систему и генерировать JWT токен")
    void shouldSignInUserAndGenerateJwtToken() {
        SignInRequest signInRequest = new SignInRequest();
        AuthInfo authInfo = new AuthInfo();
        String jwtToken = "jwtToken";

        RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(discoveryClient.getNextServerFromEureka("SERVICE-USER", false)).thenReturn(instanceInfo);
        when(instanceInfo.getHomePageUrl()).thenReturn("http://localhost:8080");
        when(restClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(signInRequest)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(AuthInfo.class)).thenReturn(authInfo);
        when(jwtService.generateToken(authInfo)).thenReturn(jwtToken);

        JwtAuthenticationResponse response = authenticationService.signIn(signInRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
    }

    @Test
    @Order(7)
    @DisplayName("Должен возвращать ошибку при входе в систему с неверным паролем")
    void shouldReturnErrorWhenSignInWithInvalidPassword() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setPassword("wrongPassword");

        when(restClient.method(HttpMethod.GET))
                .thenThrow(new RuntimeException("Invalid password"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.signIn(signInRequest);
        });

        assertEquals("Invalid password", exception.getMessage());
    }


    @Test
    @Order(8)
    @DisplayName("Должен проверять валидность токена")
    void shouldValidateToken() {
        String token = "validToken";
        String email = "test@example.com";
        Long userId = 1L;

        when(jwtService.isTokenValid(token))
                .thenReturn(true);
        when(jwtService.extractUserEmail(token))
                .thenReturn(email);
        when(jwtService.extractUserId(token))
                .thenReturn(userId);

        JwtValidateResponse response = authenticationService.validateToken(token);

        assertNotNull(response);
        assertTrue(response.isValid());
        assertEquals(email, response.getSubject());
        assertEquals(userId, response.getUserId());
    }

    @Test
    @Order(9)
    @DisplayName("Должен проверять валидность токена для другого пользователя")
    void shouldValidateTokenForAnotherUser() {
        String token = "validToken";
        String email = "another@example.com";
        Long userId = 2L;

        when(jwtService.isTokenValid(token))
                .thenReturn(true);
        when(jwtService.extractUserEmail(token))
                .thenReturn(email);
        when(jwtService.extractUserId(token))
                .thenReturn(userId);

        JwtValidateResponse response = authenticationService.validateToken(token);

        assertNotNull(response);
        assertTrue(response.isValid());
        assertEquals(email, response.getSubject());
        assertEquals(userId, response.getUserId());
    }

    @Test
    @Order(10)
    @DisplayName("Должен возвращать null для недействительного токена")
    void shouldReturnNullForInvalidToken() {
        String token = "invalidToken";

        when(jwtService.isTokenValid(token))
                .thenReturn(false);

        JwtValidateResponse response = authenticationService.validateToken(token);

        assertNull(response);
    }

    @Test
    @Order(11)
    @DisplayName("Должен возвращать null для пустого токена")
    void shouldReturnNullForEmptyToken() {
        String token = "";

        JwtValidateResponse response = authenticationService.validateToken(token);

        assertNull(response);
    }
}