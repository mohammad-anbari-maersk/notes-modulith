package dev.nils.notes_modulith;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfiguration.class)
class NotesModulithApplicationTests {

    @Autowired
    MockMvcTester mvc;

    @Test
    void contextLoads() {}

    @Test
    void onlyAllowlistedActuatorEndpointsAreExposed() {
        assertThat(mvc.get().uri("/actuator/health")).hasStatus(HttpStatus.OK);
        assertThat(mvc.get().uri("/actuator/modulith")).hasStatus(HttpStatus.OK);
        assertThat(mvc.get().uri("/actuator/loggers")).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(mvc.get().uri("/actuator/env")).hasStatus(HttpStatus.NOT_FOUND);
    }
}
