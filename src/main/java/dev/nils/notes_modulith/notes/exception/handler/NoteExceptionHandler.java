package dev.nils.notes_modulith.notes.exception.handler;

import dev.nils.notes_modulith.notes.exception.NoteException;
import dev.nils.notes_modulith.notes.web.NoteRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = NoteRestController.class)
public class NoteExceptionHandler {

    @ExceptionHandler(NoteException.class)
    ProblemDetail handleNoteException(NoteException ex) {
        HttpStatus status = switch (ex.getErrorCode()) {
            case NOTE_NOT_FOUND_WITH_ID -> HttpStatus.NOT_FOUND;
            case NOTE_CREATION_FAILED_USER_NOT_FOUND_WITH_ID -> HttpStatus.BAD_REQUEST;
        };
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setProperty("code", ex.getErrorCode().name());
        return problem;
    }
}
