package ru.ameeleon.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.ameeleon.gateway.util.JwtUtil;

import java.util.List;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RouteValidator validator;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private HttpHeaders headers;

    @Mock
    private Predicate<ServerHttpRequest> isSecuredPredicate;

    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        authenticationFilter = new AuthenticationFilter();
        validator.isSecured = isSecuredPredicate;
        ReflectionTestUtils.setField(authenticationFilter, "validator", validator);
        ReflectionTestUtils.setField(authenticationFilter, "jwtUtil", jwtUtil);
    }

    @Test
    void shouldAllowRequestWhenRouteIsNotSecured() {
        // Given
        when(isSecuredPredicate.test(request)).thenReturn(false);
        when(exchange.getRequest()).thenReturn(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain).filter(exchange);
        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthorizationHeaderIsMissing() {
        // Given
        when(isSecuredPredicate.test(request)).thenReturn(true);
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(headers);
        when(headers.containsKey(HttpHeaders.AUTHORIZATION)).thenReturn(false);
        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);
        when(response.setComplete()).thenReturn(Mono.empty());

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthorizationHeaderIsInvalid() {
        // Given
        when(isSecuredPredicate.test(request)).thenReturn(true);
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(headers);
        when(headers.containsKey(HttpHeaders.AUTHORIZATION)).thenReturn(true);
        when(headers.get(HttpHeaders.AUTHORIZATION)).thenReturn(List.of("Invalid"));
        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);
        when(response.setComplete()).thenReturn(Mono.empty());

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid() {
        // Given
        String token = "invalid.token.here";
        when(isSecuredPredicate.test(request)).thenReturn(true);
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(headers);
        when(headers.containsKey(HttpHeaders.AUTHORIZATION)).thenReturn(true);
        when(headers.get(HttpHeaders.AUTHORIZATION)).thenReturn(List.of("Bearer " + token));
        when(jwtUtil.validateToken(token)).thenReturn(false);
        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);
        when(response.setComplete()).thenReturn(Mono.empty());

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(jwtUtil).validateToken(token);
        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void shouldAllowRequestWhenTokenIsValid() {
        // Given
        String token = "valid.token.here";
        String username = "testuser";
        String role = "USER";
        
        ServerHttpRequest.Builder requestBuilder = mock(ServerHttpRequest.Builder.class);
        ServerWebExchange.Builder exchangeBuilder = mock(ServerWebExchange.Builder.class);
        ServerHttpRequest modifiedRequest = mock(ServerHttpRequest.class);
        ServerWebExchange modifiedExchange = mock(ServerWebExchange.class);
        
        when(isSecuredPredicate.test(request)).thenReturn(true);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(headers.containsKey(HttpHeaders.AUTHORIZATION)).thenReturn(true);
        when(headers.get(HttpHeaders.AUTHORIZATION)).thenReturn(List.of("Bearer " + token));
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(jwtUtil.getRoleFromToken(token)).thenReturn(role);
        when(request.mutate()).thenReturn(requestBuilder);
        when(requestBuilder.header(anyString(), anyString())).thenReturn(requestBuilder);
        when(requestBuilder.build()).thenReturn(modifiedRequest);
        when(exchange.mutate()).thenReturn(exchangeBuilder);
        when(exchangeBuilder.request(modifiedRequest)).thenReturn(exchangeBuilder);
        when(exchangeBuilder.build()).thenReturn(modifiedExchange);
        when(chain.filter(modifiedExchange)).thenReturn(Mono.empty());

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(jwtUtil).validateToken(token);
        verify(jwtUtil).getUsernameFromToken(token);
        verify(jwtUtil).getRoleFromToken(token);
        verify(requestBuilder, times(2)).header(anyString(), anyString());
    }
}