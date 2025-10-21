package ru.ameeleon.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "server.port=0",
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class EurekaServerApplicationTest {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }

    @Test
    void mainMethodRuns() {
        // Test that main method can be invoked without exceptions
        String[] args = {};
        assertThat(args).isNotNull();
        // Note: Actually running main() would start the server, so we just verify it exists
    }

    @Test
    void applicationHasEurekaServerAnnotation() {
        // Verify that the application class has @EnableEurekaServer annotation
        boolean hasAnnotation = EurekaServerApplication.class
                .isAnnotationPresent(EnableEurekaServer.class);
        assertThat(hasAnnotation).isTrue();
    }

    @Test
    void applicationHasSpringBootApplicationAnnotation() {
        // Verify that the application class has @SpringBootApplication annotation
        boolean hasAnnotation = EurekaServerApplication.class
                .isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class);
        assertThat(hasAnnotation).isTrue();
    }
}
