package ru.ameeleon.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteValidatorTest {

    @Mock
    private ServerHttpRequest request;

    private RouteValidator routeValidator;

    @BeforeEach
    void setUp() {
        routeValidator = new RouteValidator();
    }

    @Test
    void shouldAllowAccessToRegisterEndpoint() {
        // Given
        when(request.getURI()).thenReturn(URI.create("/api/auth/register"));

        // When
        boolean isSecured = routeValidator.isSecured.test(request);

        // Then
        assertThat(isSecured).isFalse();
    }

    @Test
    void shouldAllowAccessToLoginEndpoint() {
        // Given
        when(request.getURI()).thenReturn(URI.create("/api/auth/login"));

        // When
        boolean isSecured = routeValidator.isSecured.test(request);

        // Then
        assertThat(isSecured).isFalse();
    }

    @Test
    void shouldAllowAccessToUserRegisterEndpoint() {
        // Given
        when(request.getURI()).thenReturn(URI.create("/user/register"));

        // When
        boolean isSecured = routeValidator.isSecured.test(request);

        // Then
        assertThat(isSecured).isFalse();
    }

    @Test
    void shouldAllowAccessToUserAuthEndpoint() {
        // Given
        when(request.getURI()).thenReturn(URI.create("/user/auth"));

        // When
        boolean isSecured = routeValidator.isSecured.test(request);

        // Then
        assertThat(isSecured).isFalse();
    }

    @Test
    void shouldAllowAccessToEurekaEndpoint() {
        // Given
        when(request.getURI()).thenReturn(URI.create("/eureka"));

        // When
        boolean isSecured = routeValidator.isSecured.test(request);

        // Then
        assertThat(isSecured).isFalse();
    }

    @Test
    void shouldAllowAccessToEurekaSubPath() {
        // Given
        when(request.getURI()).thenReturn(URI.create("/eureka/apps"));

        // When
        boolean isSecured = routeValidator.isSecured.test(request);

        // Then
        assertThat(isSecured).isFalse();
    }

    @Test
    void shouldSecureProtectedEndpoint() {
        // Given
        when(request.getURI()).thenReturn(URI.create("/api/bookings"));

        // When
        boolean isSecured = routeValidator.isSecured.test(request);

        // Then
        assertThat(isSecured).isTrue();
    }

    @Test
    void shouldSecureHotelEndpoint() {
        // Given
        when(request.getURI()).thenReturn(URI.create("/api/hotels"));

        // When
        boolean isSecured = routeValidator.isSecured.test(request);

        // Then
        assertThat(isSecured).isTrue();
    }

    @Test
    void shouldSecureUserProfileEndpoint() {
        // Given
        when(request.getURI()).thenReturn(URI.create("/api/users/profile"));

        // When
        boolean isSecured = routeValidator.isSecured.test(request);

        // Then
        assertThat(isSecured).isTrue();
    }

    @Test
    void shouldSecureRandomEndpoint() {
        // Given
        when(request.getURI()).thenReturn(URI.create("/api/some/protected/path"));

        // When
        boolean isSecured = routeValidator.isSecured.test(request);

        // Then
        assertThat(isSecured).isTrue();
    }

    @Test
    void shouldVerifyOpenApiEndpointsListIsNotEmpty() {
        // Then
        assertThat(RouteValidator.openApiEndpoints).isNotEmpty();
        assertThat(RouteValidator.openApiEndpoints).hasSize(5);
    }

    @Test
    void shouldVerifyOpenApiEndpointsContainExpectedValues() {
        // Then
        assertThat(RouteValidator.openApiEndpoints).contains(
                "/api/auth/register",
                "/api/auth/login",
                "/user/register",
                "/user/auth",
                "/eureka"
        );
    }
}