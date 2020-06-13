package accenture.demo.capacity;

import accenture.demo.exception.entry.EntryDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CapacityExceptionHandler {

    @ExceptionHandler(value = EntryDeniedException.class)
    public ResponseEntity<?> handleEntryDeniedException(EntryDeniedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }
}
