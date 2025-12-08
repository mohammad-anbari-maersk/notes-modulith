package dev.nils.notes_modulith.notes.domain;

import dev.nils.notes_modulith.notes.domain.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {}
