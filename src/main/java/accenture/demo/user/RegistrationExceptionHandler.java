package accenture.demo.user;


import accenture.demo.exception.registration.EmailAddressIsAlreadyRegisteredException;
import accenture.demo.registration.RegistrationResponseDTO;
import accenture.demo.exception.registration.RequestBodyIsNullException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RegistrationExceptionHandler {


  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = RequestBodyIsNullException.class)
  public ResponseEntity<?> handleNullRequestBody(RequestBodyIsNullException ex) {
    String message = ex.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new RegistrationResponseDTO(null, null, null,
                    "error", null, message));
  }

  @ExceptionHandler(value = EmailAddressIsAlreadyRegisteredException.class)
  public ResponseEntity<?> emailAddressIsAlreadyRegistered(EmailAddressIsAlreadyRegisteredException ex) {
    String message = ex.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new RegistrationResponseDTO(null, null, null,
                    "error", null, message));
  }
}
