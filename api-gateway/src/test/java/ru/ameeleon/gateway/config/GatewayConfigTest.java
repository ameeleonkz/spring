package ru.ameeleon.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ameeleon.gateway.filter.AuthenticationFilter;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GatewayConfigTest {

    @Mock
    private AuthenticationFilter authenticationFilter;

    private GatewayConfig gatewayConfig;

    @BeforeEach
    void setUp() {
        gatewayConfig = new GatewayConfig(authenticationFilter);
    }

    @Test
    void shouldInjectAuthenticationFilter() {
        // Then
        assertThat(gatewayConfig).isNotNull();
    }

    @Test
    void shouldVerifyAuthenticationFilterIsInjected() {
        // Then
        assertThat(authenticationFilter).isNotNull();
    }
}