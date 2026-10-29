package dev.nils.notes_modulith.users.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

import dev.nils.notes_modulith.users.domain.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    UserService userService;

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"fullName\":\"No Email\"}", "{\"email\":\"not-an-email\"}", "{\"email\":\" \"}"})
    @DisplayName("POST /users rejects invalid requests with 400 before reaching the service")
    void create_invalidRequest_returns400(String body) {
        assertThat(mvc.post().uri("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .hasStatus(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(userService);
    }
}
