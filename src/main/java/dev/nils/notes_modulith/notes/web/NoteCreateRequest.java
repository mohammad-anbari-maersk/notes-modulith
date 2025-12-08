package dev.nils.notes_modulith.notes.web;

public record NoteCreateRequest(Long userId, String title, String content) {}
