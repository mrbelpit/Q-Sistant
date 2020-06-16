package accenture.demo.capacity;

import accenture.demo.exception.appuser.CardIdNotExistException;
import accenture.demo.exception.capacity.CapacitySetupException;
import accenture.demo.exception.capacity.InvalidCapacitySetupModifierException;
import accenture.demo.exception.capacity.InvalidCapacitySetupValueException;
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

  @ExceptionHandler(value = CapacitySetupException.class)
  public ResponseEntity<?> handleCapacitySetupException(CapacitySetupException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = InvalidCapacitySetupModifierException.class)
  public ResponseEntity<?> handleInvalidCapacitySetupModifierException(
      InvalidCapacitySetupModifierException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = InvalidCapacitySetupValueException.class)
  public ResponseEntity<?> handleInvalidCapacitySetupValueException(
      InvalidCapacitySetupValueException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = CardIdNotExistException.class)
  public ResponseEntity<?> handleCardIdNotExistException(
      CardIdNotExistException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.OK);
  }
}
