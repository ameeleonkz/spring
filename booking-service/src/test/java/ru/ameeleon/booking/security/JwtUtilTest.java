package ru.ameeleon.booking.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;
    private String testSecret;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        testSecret = "mySecretKeyForTestingPurposesOnly12345678901234567890";
        
        // Используем ReflectionTestUtils для установки приватного поля secret
        ReflectionTestUtils.setField(jwtUtil, "secretKey", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 3600000L); // 1 час
        
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtUtil.generateToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken(userDetails);
        
        String username = jwtUtil.extractUsername(token);
        
        assertEquals("testuser", username);
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        String token = jwtUtil.generateToken(userDetails);
        
        Date expiration = jwtUtil.extractExpiration(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtUtil.generateToken(userDetails);
        
        boolean isValid = jwtUtil.validateToken(token, userDetails);
        
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForWrongUsername() {
        String token = jwtUtil.generateToken(userDetails);
        
        UserDetails wrongUser = User.builder()
                .username("wronguser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
        
        boolean isValid = jwtUtil.validateToken(token, wrongUser);
        
        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_ShouldReturnFalse_ForNewToken() {
        String token = jwtUtil.generateToken(userDetails);
        
        boolean isExpired = jwtUtil.isTokenExpired(token);
        
        assertFalse(isExpired);
    }

    @Test
    void extractAllClaims_ShouldReturnClaims() {
        String token = jwtUtil.generateToken(userDetails);
        
        Claims claims = jwtUtil.extractAllClaims(token);
        
        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
    }

    @Test
    void generateToken_WithClaims_ShouldIncludeClaims() {
        String token = jwtUtil.generateToken(userDetails);
        Claims claims = jwtUtil.extractAllClaims(token);
        
        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
}