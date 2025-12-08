package dev.nils.notes_modulith.notes.web;

import dev.nils.notes_modulith.notes.domain.NoteService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
public class NoteRestController {

    private final NoteService noteService;

    public NoteRestController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<NoteDto> findAll() {
        return noteService.findAll();
    }

    @GetMapping("/{id}")
    public NoteDto findById(@PathVariable Long id) {
        return noteService.findById(id);
    }

    @PostMapping
    public NoteDto create(@RequestBody NoteCreateRequest req) {
        return noteService.create(req);
    }

    @PutMapping("/{id}")
    public NoteDto update(@PathVariable Long id, @RequestBody NoteUpdateRequest req) {
        return noteService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        noteService.delete(id);
    }
}
