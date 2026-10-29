package dev.nils.notes_modulith;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * One PostgreSQL and one RabbitMQ container shared by every test context in the JVM.
 *
 * <p>The containers are owned by the JVM (stopped by Testcontainers' Ryuk on exit), not by a Spring context:
 * {@code destroyMethod = ""} keeps the first context that closes from stopping containers that other cached
 * contexts still use.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer(DockerImageName.parse("postgres:18-alpine"));

    private static final RabbitMQContainer RABBITMQ =
            new RabbitMQContainer(DockerImageName.parse("rabbitmq:4.2.1-alpine"));

    static {
        POSTGRES.start();
        RABBITMQ.start();
    }

    @Bean(destroyMethod = "")
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return POSTGRES;
    }

    @Bean(destroyMethod = "")
    @ServiceConnection
    RabbitMQContainer rabbitmq() {
        return RABBITMQ;
    }
}
