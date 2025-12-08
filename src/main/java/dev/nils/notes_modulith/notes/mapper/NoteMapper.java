package dev.nils.notes_modulith.notes.mapper;

import dev.nils.notes_modulith.notes.domain.model.Note;
import dev.nils.notes_modulith.notes.web.NoteCreateRequest;
import dev.nils.notes_modulith.notes.web.NoteDto;
import dev.nils.notes_modulith.notes.web.NoteUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    NoteDto toDto(Note note);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Note fromCreateRequest(NoteCreateRequest req, Long userId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void applyUpdate(NoteUpdateRequest req, @MappingTarget Note note);
}
