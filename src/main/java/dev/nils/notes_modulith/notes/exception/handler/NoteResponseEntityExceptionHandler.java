package dev.nils.notes_modulith.notes.exception.handler;

import dev.nils.notes_modulith.notes.exception.NoteException;
import dev.nils.notes_modulith.notes.exception.model.NoteExceptionResponse;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class NoteResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NoteException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleNoteExceptions(Exception ex, WebRequest request) {
        NoteExceptionResponse exceptionResponse =
                new NoteExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
