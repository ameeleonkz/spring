package ru.ameeleon.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ameeleon.gateway.filter.AuthenticationFilter;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;

    public GatewayConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Booking Service Routes
                .route("booking-service", r -> r
                        .path("/user/**", "/booking/**", "/bookings/**", "/api/auth/**", "/api/bookings/**", "/api/users/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://booking-service"))

                // Hotel Service Routes
                .route("hotel-service", r -> r
                        .path("/api/hotels/**", "/api/rooms/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://hotel-service"))

                .build();
    }
}