package dev.nils.notes_modulith.notes.domain;

import dev.nils.notes_modulith.notes.domain.model.Note;
import dev.nils.notes_modulith.notes.domain.model.NoteCreatedEvent;
import dev.nils.notes_modulith.notes.exception.NoteException;
import dev.nils.notes_modulith.notes.exception.model.NoteErrorCode;
import dev.nils.notes_modulith.notes.mapper.NoteMapper;
import dev.nils.notes_modulith.notes.web.NoteCreateRequest;
import dev.nils.notes_modulith.notes.web.NoteDto;
import dev.nils.notes_modulith.notes.web.NoteUpdateRequest;
import dev.nils.notes_modulith.users.UserApi;
import dev.nils.notes_modulith.users.UserDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    private final UserApi userApi;

    private final NoteMapper noteMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<NoteDto> findAll() {
        return noteRepository.findAll().stream().map(noteMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public NoteDto findById(Long id) {
        return noteMapper.toDto(getExisting(id));
    }

    @Transactional
    public NoteDto create(NoteCreateRequest createRequest) {
        UserDto user = userApi.findById(createRequest.userId()).orElseThrow(() -> {
            log.warn("[NOTES_SERVICE][CREATE][FAILED]: User not found with id={}", createRequest.userId());
            return new NoteException(NoteErrorCode.NOTE_CREATION_FAILED_USER_NOT_FOUND_WITH_ID, createRequest.userId());
        });

        Note note = noteMapper.fromCreateRequest(createRequest, user.id());
        Note savedNote = noteRepository.save(note);

        log.info("[NOTES_SERVICE][CREATE]: Created a note with id={}", savedNote.getId());
        NoteCreatedEvent event = new NoteCreatedEvent(savedNote.getId(), savedNote.getTitle());
        eventPublisher.publishEvent(event);

        return noteMapper.toDto(savedNote);
    }

    @Transactional
    public NoteDto update(Long id, NoteUpdateRequest req) {
        Note existing = getExisting(id);
        noteMapper.applyUpdate(req, existing);

        // Flush so @PreUpdate sets updatedAt before the response is mapped.
        return noteMapper.toDto(noteRepository.saveAndFlush(existing));
    }

    @Transactional
    public void delete(Long id) {
        noteRepository.deleteById(id);
    }

    private Note getExisting(Long id) {
        return noteRepository.findById(id).orElseThrow(() -> {
            log.warn("[NOTE_SERVICE] [FAILED] [NOTE_NOT_FOUND_WITH_ID] [ID={}]", id);
            return new NoteException(NoteErrorCode.NOTE_NOT_FOUND_WITH_ID, id);
        });
    }
}
