package org.atlas.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.atlas.rest.dto.AuthInfo;
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
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private AuthInfo authInfo;

    private final String secret = "E2Sn66b1zPLNf9IxLcBG8x8r9Ody1Bi3Vsqc0/5o3KKfdgHvnxAB3Z3dPh2LCDek";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", secret);
    }

    @Test
    @Order(1)
    @DisplayName("Должен извлекать email пользователя из токена")
    void shouldExtractUserEmail() {
        String email = "test@example.com";
        String token = generateTestToken(email, 1L);

        String extractedEmail = jwtService.extractUserEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    @Order(2)
    @DisplayName("Должен извлекать ID пользователя из токена")
    void shouldExtractUserId() {
        Long userId = 1L;
        String token = generateTestToken("test@example.com", userId);

        Long extractedUserId = jwtService.extractUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    @Order(3)
    @DisplayName("Должен генерировать токен")
    void shouldGenerateToken() {
        when(authInfo.getUserId()).thenReturn(1L);
        when(authInfo.getEmail()).thenReturn("test@example.com");

        String token = jwtService.generateToken(authInfo);

        assertNotNull(token);
        assertEquals("test@example.com", jwtService.extractUserEmail(token));
        assertEquals(1L, jwtService.extractUserId(token));
    }

    @Test
    @Order(4)
    @DisplayName("Должен проверять валидность токена")
    void shouldValidateToken() {
        String token = generateTestToken("test@example.com", 1L);

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    @Order(5)
    @DisplayName("Должен проверять просроченность токена")
    void shouldCheckIfTokenIsExpired() {
        String expiredToken = generateExpiredTestToken("test@example.com", 1L);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken));
    }

    private String generateTestToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration
                .signWith(getSigningKey(), SIG.HS256)
                .compact();
    }

    private String generateExpiredTestToken(String email, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 hour ago
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60 * 30)) // 30 minutes ago
                .signWith(getSigningKey(), SIG.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}