package dev.nils.notes_modulith.notes.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

import dev.nils.notes_modulith.notes.domain.NoteService;
import dev.nils.notes_modulith.notes.exception.NoteException;
import dev.nils.notes_modulith.notes.exception.model.NoteErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(NoteRestController.class)
class NoteRestControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    NoteService noteService;

    @Test
    @DisplayName("GET /notes/{id} returns 404 problem detail when the note does not exist")
    void findById_missingNote_returns404() {
        given(noteService.findById(42L)).willThrow(new NoteException(NoteErrorCode.NOTE_NOT_FOUND_WITH_ID, 42L));

        assertThat(mvc.get().uri("/notes/42"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("NOTE_NOT_FOUND_WITH_ID");
    }

    @Test
    @DisplayName("PUT /notes/{id} returns 404 when the note does not exist")
    void update_missingNote_returns404() {
        given(noteService.update(eq(42L), any()))
                .willThrow(new NoteException(NoteErrorCode.NOTE_NOT_FOUND_WITH_ID, 42L));

        assertThat(mvc.put().uri("/notes/42").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"t\",\"content\":\"c\"}"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("POST /notes returns 400 when the user does not exist")
    void create_unknownUser_returns400() {
        given(noteService.create(any()))
                .willThrow(new NoteException(NoteErrorCode.NOTE_CREATION_FAILED_USER_NOT_FOUND_WITH_ID, 9L));

        assertThat(mvc.post().uri("/notes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":9,\"title\":\"t\",\"content\":\"c\"}"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("NOTE_CREATION_FAILED_USER_NOT_FOUND_WITH_ID");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{}",
        "{\"title\":\"t\",\"content\":\"c\"}",
        "{\"userId\":0,\"title\":\"t\",\"content\":\"c\"}",
        "{\"userId\":1,\"title\":\"  \",\"content\":\"c\"}",
        "{\"userId\":1,\"content\":\"c\"}"
    })
    @DisplayName("POST /notes rejects invalid requests with 400 before reaching the service")
    void create_invalidRequest_returns400(String body) {
        assertThat(mvc.post().uri("/notes").contentType(MediaType.APPLICATION_JSON).content(body))
                .hasStatus(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(noteService);
    }

    @Test
    @DisplayName("POST /notes rejects a title longer than the 1000-character column")
    void create_oversizedTitle_returns400() {
        String body = "{\"userId\":1,\"title\":\"" + "x".repeat(1001) + "\"}";

        assertThat(mvc.post().uri("/notes").contentType(MediaType.APPLICATION_JSON).content(body))
                .hasStatus(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(noteService);
    }

    @Test
    @DisplayName("PUT /notes/{id} rejects a blank title with 400")
    void update_blankTitle_returns400() {
        assertThat(mvc.put().uri("/notes/1").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\" \"}"))
                .hasStatus(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(noteService);
    }
}
