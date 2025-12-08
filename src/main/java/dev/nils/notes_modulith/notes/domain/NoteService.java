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
        return noteRepository.findById(id).map(noteMapper::toDto).orElseThrow(() -> {
            log.error("[NOTE_SERVICE] [FAILED] [NOTE_NOT_FOUND_WITH_ID] [ID={}]", id);
            return new NoteException(NoteErrorCode.NOTE_NOT_FOUND_WITH_ID, id);
        });
    }

    @Transactional
    public NoteDto create(NoteCreateRequest createRequest) {
        UserDto user;
        try {
            user = userApi.getById(createRequest.userId());
        } catch (Exception e) {
            log.error("[NOTES_SERVICE][CREATE][FAILED]: User not found with id={}", createRequest.userId());
            throw new NoteException(NoteErrorCode.NOTE_CREATION_FAILED_USER_NOT_FOUND_WITH_ID, createRequest.userId());
        }

        Note note = noteMapper.fromCreateRequest(createRequest, user.id());
        Note savedNote = noteRepository.save(note);

        log.info("[NOTES_SERVICE][CREATE]: Created a note with id={}", savedNote.getId());
        NoteCreatedEvent event = new NoteCreatedEvent(savedNote.getId(), savedNote.getTitle());
        eventPublisher.publishEvent(event);

        return noteMapper.toDto(savedNote);
    }

    @Transactional
    public NoteDto update(Long id, NoteUpdateRequest req) {
        Note existing = noteRepository.findById(id).orElseThrow();
        noteMapper.applyUpdate(req, existing);

        return noteMapper.toDto(noteRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        noteRepository.deleteById(id);
    }
}
