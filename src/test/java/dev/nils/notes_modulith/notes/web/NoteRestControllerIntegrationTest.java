package dev.nils.notes_modulith.notes.web;

import dev.nils.notes_modulith.TestContainersConfiguration;
import dev.nils.notes_modulith.users.web.UserCreateRequest;
import dev.nils.notes_modulith.users.UserDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApplicationModuleTest(
        webEnvironment = RANDOM_PORT,
        extraIncludes = {"common", "users", "notifications", "notes"})
@Import({TestContainersConfiguration.class})
@AutoConfigureMockMvc
class NoteRestControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final String BASE_URL = "/notes";
    private static final String USERS_BASE_URL = "/users";

    @Test
    @DisplayName("GET /api/notes should return all notes from database")
    void getAllNotes_shouldReturnSeededNotes() throws Exception {

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].title").isNotEmpty())
                .andExpect(jsonPath("$[*].content").isNotEmpty())
                .andExpect(jsonPath("$[*].userId").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/notes creates a new note and returns NoteDto")
    void createNote_shouldReturnCreatedNote() throws Exception {
        // First create a user to attach the note to
        UserCreateRequest createUserReq = new UserCreateRequest("note.user@example.com", "Note User");

        ResultActions userResponse = mockMvc.perform(post(USERS_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserReq)));

        String userJson = userResponse.andReturn().getResponse().getContentAsString();
        UserDto userDto = objectMapper.readValue(userJson, UserDto.class);

        // Now create the note
        NoteCreateRequest noteReq = new NoteCreateRequest(userDto.id(), "Test Note", "Integration test content");

        ResultActions noteResponse = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteReq)));

        noteResponse
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Test Note"))
                .andExpect(jsonPath("$.content").value("Integration test content"))
                .andExpect(jsonPath("$.userId").value(userDto.id()));

        String noteJson = noteResponse.andReturn().getResponse().getContentAsString();
        NoteDto noteDto = objectMapper.readValue(noteJson, NoteDto.class);
        assertThat(noteDto.id()).isNotNull();
    }

    @Test
    @DisplayName("POST /api/notes with non-existing userId should return 400")
    void createNote_withUnknownUser_shouldReturnBadRequest() throws Exception {
        NoteCreateRequest noteReq = new NoteCreateRequest(999_999L, "Orphan Note", "Should fail");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteReq)))
                .andExpect(status().isBadRequest());
    }
}
