package dev.nils.notes_modulith.users.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.nils.notes_modulith.TestContainersConfiguration;
import dev.nils.notes_modulith.users.UserDto;
import dev.nils.notes_modulith.users.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

@ApplicationModuleTest(
        webEnvironment = RANDOM_PORT,
        extraIncludes = {"common", "notes"})
@Import(TestContainersConfiguration.class)
@AutoConfigureMockMvc
class UserRestControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    private static final String BASE_URL = "/users";

    @Test
    @DisplayName("POST /api/users creates a new user and returns UserDto")
    void createUser_shouldReturnCreatedUser() throws Exception {

        UserCreateRequest req = new UserCreateRequest("new.user@example.com", "New User");

        // When
        ResultActions response = mockMvc.perform(
                post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value("new.user@example.com"))
                .andExpect(jsonPath("$.fullName").value("New User"));

        String json = response.andReturn().getResponse().getContentAsString();
        UserDto dto = objectMapper.readValue(json, UserDto.class);

        assertThat(userRepository.findById(dto.id()))
                .hasValueSatisfying(user -> assertThat(user.getEmail()).isEqualTo("new.user@example.com"));
    }

    @Test
    @DisplayName("GET /api/users should return all users from database")
    void getAllUsers_shouldReturnSeededUsers() throws Exception {

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].email").isNotEmpty())
                .andExpect(jsonPath("$[*].fullName").isNotEmpty());
    }
}
