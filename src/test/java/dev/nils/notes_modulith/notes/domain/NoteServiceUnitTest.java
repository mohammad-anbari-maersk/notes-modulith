package dev.nils.notes_modulith.notes.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.nils.notes_modulith.notes.domain.model.Note;
import dev.nils.notes_modulith.notes.domain.model.NoteCreatedEvent;
import dev.nils.notes_modulith.notes.exception.NoteException;
import dev.nils.notes_modulith.notes.mapper.NoteMapper;
import dev.nils.notes_modulith.notes.web.NoteCreateRequest;
import dev.nils.notes_modulith.notes.web.NoteDto;
import dev.nils.notes_modulith.notes.web.NoteUpdateRequest;
import dev.nils.notes_modulith.users.UserApi;
import dev.nils.notes_modulith.users.UserDto;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class NoteServiceUnitTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserApi userApi;

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private NoteService noteService;

    @Test
    @DisplayName("findAll() should return mapped dtos")
    void findAll_shouldReturnDtos() {
        Note n1 = new Note();
        Note n2 = new Note();

        when(noteRepository.findAll()).thenReturn(List.of(n1, n2));

        NoteDto d1 = new NoteDto(1L, Instant.now(), Instant.now(), "t1", "c1", 11L);
        NoteDto d2 = new NoteDto(2L, Instant.now(), Instant.now(), "t2", "c2", 22L);

        when(noteMapper.toDto(n1)).thenReturn(d1);
        when(noteMapper.toDto(n2)).thenReturn(d2);

        List<NoteDto> result = noteService.findAll();

        assertThat(result).containsExactly(d1, d2);
        verify(noteRepository).findAll();
        verify(noteMapper).toDto(n1);
        verify(noteMapper).toDto(n2);
    }

    @Test
    @DisplayName("findById() should return dto when note exists")
    void findById_shouldReturnDto() {
        Note note = new Note();
        note.setId(10L);

        NoteDto dto = new NoteDto(10L, Instant.now(), Instant.now(), "title", "content", 5L);

        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));
        when(noteMapper.toDto(note)).thenReturn(dto);

        NoteDto result = noteService.findById(10L);

        assertThat(result).isEqualTo(dto);
        verify(noteRepository).findById(10L);
        verify(noteMapper).toDto(note);
    }

    @Test
    @DisplayName("findById() should throw NoteException when not found")
    void findById_shouldThrowWhenMissing() {
        when(noteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.findById(999L))
                .isInstanceOf(NoteException.class)
                .hasMessageContaining("Note record cannot be found for id value");

        verify(noteRepository).findById(999L);
        verifyNoInteractions(noteMapper);
    }

    @Test
    @DisplayName("create() should call UserApi, save note, publish event, and return dto")
    void create_shouldMapSavePublishAndReturnDto() {
        Long userId = 7L;
        NoteCreateRequest req = new NoteCreateRequest(userId, "New title", "New content");

        UserDto userDto = new UserDto(userId, "user@example.com", "User");
        Note mapped = new Note();
        Note saved = new Note();
        saved.setId(123L);
        saved.setTitle("New title");

        NoteDto dto = new NoteDto(123L, Instant.now(), Instant.now(), "New title", "New content", userId);

        when(userApi.findById(userId)).thenReturn(Optional.of(userDto));
        when(noteMapper.fromCreateRequest(req, userId)).thenReturn(mapped);
        when(noteRepository.save(mapped)).thenReturn(saved);
        when(noteMapper.toDto(saved)).thenReturn(dto);

        NoteDto result = noteService.create(req);

        assertThat(result).isEqualTo(dto);
        verify(userApi).findById(userId);
        verify(noteMapper).fromCreateRequest(req, userId);
        verify(noteRepository).save(mapped);
        verify(noteMapper).toDto(saved);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(NoteCreatedEvent.class);
        NoteCreatedEvent published = (NoteCreatedEvent) eventCaptor.getValue();
        assertThat(published.noteId()).isEqualTo(123L);
        assertThat(published.title()).isEqualTo("New title");
    }

    @Test
    @DisplayName("create() should throw NoteException when user not found")
    void create_shouldThrowWhenUserNotFound() {
        Long userId = 77L;
        NoteCreateRequest req = new NoteCreateRequest(userId, "T", "C");

        when(userApi.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.create(req))
                .isInstanceOf(NoteException.class)
                .hasMessageContaining("Note creation failed, user not found with id value");

        verify(userApi).findById(userId);
        verifyNoInteractions(noteMapper);
        verifyNoInteractions(noteRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("create() should propagate unexpected user lookup failures unchanged")
    void create_shouldPropagateUnexpectedUserLookupFailure() {
        NoteCreateRequest req = new NoteCreateRequest(7L, "T", "C");
        IllegalStateException failure = new IllegalStateException("database down");

        when(userApi.findById(7L)).thenThrow(failure);

        assertThatThrownBy(() -> noteService.create(req)).isSameAs(failure);
        verifyNoInteractions(noteRepository, eventPublisher);
    }

    @Test
    @DisplayName("update() should throw NoteException when note not found")
    void update_shouldThrowWhenMissing() {
        when(noteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.update(999L, new NoteUpdateRequest("t", "c")))
                .isInstanceOf(NoteException.class)
                .hasMessageContaining("Note record cannot be found for id value");

        verify(noteRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should apply update and save")
    void update_shouldApplyAndSave() {
        Long id = 5L;
        NoteUpdateRequest req = new NoteUpdateRequest("updated", "updated content");
        Note existing = new Note();
        existing.setId(id);

        Note saved = new Note();
        saved.setId(id);
        NoteDto dto = new NoteDto(id, Instant.now(), Instant.now(), "updated", "updated content", 3L);

        when(noteRepository.findById(id)).thenReturn(Optional.of(existing));
        // applyUpdate is void – just verify it's called
        when(noteRepository.save(existing)).thenReturn(saved);
        when(noteMapper.toDto(saved)).thenReturn(dto);

        NoteDto result = noteService.update(id, req);

        assertThat(result).isEqualTo(dto);
        verify(noteRepository).findById(id);
        verify(noteMapper).applyUpdate(req, existing);
        verify(noteRepository).save(existing);
        verify(noteMapper).toDto(saved);
    }

    @Test
    @DisplayName("delete() should call repository.deleteById")
    void delete_shouldCallRepository() {
        noteService.delete(42L);
        verify(noteRepository).deleteById(42L);
        verifyNoInteractions(noteMapper, userApi, eventPublisher);
    }
}
