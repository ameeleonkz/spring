package ru.ameeleon.gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secretKey;
    private Key signingKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        secretKey = "mySecretKeyForTestingPurposesOnly1234567890";
        signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        
        ReflectionTestUtils.setField(jwtUtil, "secret", secretKey);
    }

    @Test
    void shouldValidateValidToken() {
        // Given
        String token = createValidToken("testuser", "USER");

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldInvalidateExpiredToken() {
        // Given
        String token = createExpiredToken("testuser", "USER");

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldInvalidateMalformedToken() {
        // Given
        String token = "invalid.token.format";

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldInvalidateNullToken() {
        // When
        boolean isValid = jwtUtil.validateToken(null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldInvalidateEmptyToken() {
        // When
        boolean isValid = jwtUtil.validateToken("");

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldExtractUsernameFromToken() {
        // Given
        String username = "testuser";
        String token = createValidToken(username, "USER");

        // When
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // Then
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void shouldExtractRoleFromToken() {
        // Given
        String role = "ADMIN";
        String token = createValidToken("testuser", role);

        // When
        String extractedRole = jwtUtil.getRoleFromToken(token);

        // Then
        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    void shouldExtractMultipleClaimsFromToken() {
        // Given
        String username = "john.doe";
        String role = "MANAGER";
        String token = createValidToken(username, role);

        // When
        String extractedUsername = jwtUtil.getUsernameFromToken(token);
        String extractedRole = jwtUtil.getRoleFromToken(token);

        // Then
        assertThat(extractedUsername).isEqualTo(username);
        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    void shouldHandleTokenWithDifferentRoles() {
        // Given
        String[] roles = {"USER", "ADMIN", "MODERATOR", "GUEST"};

        for (String role : roles) {
            String token = createValidToken("testuser", role);

            // When
            String extractedRole = jwtUtil.getRoleFromToken(token);

            // Then
            assertThat(extractedRole).isEqualTo(role);
        }
    }

    @Test
    void shouldHandleTokenWithSpecialCharactersInUsername() {
        // Given
        String username = "user@example.com";
        String token = createValidToken(username, "USER");

        // When
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // Then
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void shouldInvalidateTokenSignedWithDifferentKey() {
        // Given
        Key differentKey = Keys.hmacShaKeyFor("differentSecretKey1234567890123456".getBytes());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(differentKey, SignatureAlgorithm.HS256)
                .compact();

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    private String createValidToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String createExpiredToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 hour ago
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}