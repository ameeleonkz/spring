package ru.ameeleon.gateway;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GatewayApplicationTest {

    @Test
    void contextLoads() {
        // Verify that the application context loads successfully
    }

    @Test
    void mainMethodRunsWithoutException() {
        // Mock the Spring context to avoid starting the web server
        try (MockedStatic<SpringApplication> mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            mockedSpringApplication.when(() -> SpringApplication.run(GatewayApplication.class, new String[]{}))
                                    .thenReturn(null);
            assertDoesNotThrow(() -> GatewayApplication.main(new String[]{}));
        }
    }
}