package dev.nils.notes_modulith.notes.domain;

import dev.nils.notes_modulith.TestContainersConfiguration;
import dev.nils.notes_modulith.notes.domain.model.Note;
import dev.nils.notes_modulith.notes.exception.NoteException;
import dev.nils.notes_modulith.notes.web.NoteCreateRequest;
import dev.nils.notes_modulith.notes.web.NoteDto;
import dev.nils.notes_modulith.notes.web.NoteUpdateRequest;
import dev.nils.notes_modulith.users.domain.User;
import dev.nils.notes_modulith.users.domain.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestContainersConfiguration.class)
@Transactional
class NoteServiceIntegrationTest {

    @Autowired
    NoteService noteService;

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("findAll() should return notes from data.sql")
    void findAll_shouldReturnAllSeedNotes() {
        List<NoteDto> notes = noteService.findAll();

        assertThat(notes)
                .isNotEmpty()
                .extracting(NoteDto::title)
                .contains("Welcome note", "Todo", "Quick idea", "Meeting notes");
    }

    @Test
    @DisplayName("findById() should return note when exists")
    void findById_shouldReturnNote() {
        // pick an existing note id by reading from repository
        Long existingId =
                noteRepository.findAll().stream().findFirst().orElseThrow().getId();

        NoteDto dto = noteService.findById(existingId);

        assertThat(dto.id()).isEqualTo(existingId);
        assertThat(dto.title()).isNotBlank();
        assertThat(dto.content()).isNotBlank();
    }

    @Test
    @DisplayName("findById() should throw NoteException when ID does not exist")
    void findById_shouldThrowExceptionWhenNotExists() {
        assertThatThrownBy(() -> noteService.findById(9_999_999L))
                .isInstanceOf(NoteException.class)
                .hasMessageContaining("Note record cannot be found for id value");
    }

    @Test
    @DisplayName("create() should persist and return saved note when user exists")
    void create_shouldPersistNote() {
        // find any existing user to attach note to (from seed data)
        Long userId = userRepository
                .findByEmail("jane.doe@example.com")
                .map(User::getId)
                .orElseGet(() -> userRepository.findAll().stream()
                        .findFirst()
                        .map(User::getId)
                        .orElseThrow());

        NoteCreateRequest req = new NoteCreateRequest(userId, "Test Note from Service", "Service integration content");

        NoteDto saved = noteService.create(req);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.userId()).isEqualTo(userId);
        assertThat(saved.title()).isEqualTo("Test Note from Service");
        assertThat(saved.content()).isEqualTo("Service integration content");

        Optional<Note> persisted = noteRepository.findById(saved.id());
        assertThat(persisted).isPresent();
    }

    @Test
    @DisplayName("update() should return the persisted updatedAt and keep fields that are not sent")
    void update_shouldReturnPersistedState() {
        Note existing = noteRepository.findAll().stream().findFirst().orElseThrow();
        String originalContent = existing.getContent();

        NoteDto updated = noteService.update(existing.getId(), new NoteUpdateRequest("Updated title", null));
        entityManager.flush();
        entityManager.clear();

        Note persisted = noteRepository.findById(existing.getId()).orElseThrow();
        assertThat(updated.title()).isEqualTo("Updated title").isEqualTo(persisted.getTitle());
        assertThat(updated.content()).isEqualTo(originalContent);
        assertThat(updated.updatedAt()).isEqualTo(persisted.getUpdatedAt()).isAfter(updated.createdAt());
    }

    @Test
    @DisplayName("create() should throw NoteException when userId does not exist")
    void create_shouldThrowWhenUserNotFound() {
        NoteCreateRequest req = new NoteCreateRequest(8_888_888L, "Orphan Note", "Should fail");

        assertThatThrownBy(() -> noteService.create(req))
                .isInstanceOf(NoteException.class)
                .hasMessageContaining("Note creation failed, user not found with id value");
    }
}
