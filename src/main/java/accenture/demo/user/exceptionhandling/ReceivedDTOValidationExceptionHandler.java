package accenture.demo.user.exceptionhandling;

import accenture.demo.exception.RequestBodyIsNullException;
import accenture.demo.login.LoginResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ReceivedDTOValidationExceptionHandler {


  @ExceptionHandler(value = RequestBodyIsNullException.class)
  public ResponseEntity<LoginResponseDTO> handleNullDTO(RequestBodyIsNullException e) {
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new LoginResponseDTO("error", null,
                    e.getMessage()));
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleRegistrationFieldValidationExceptions(
          MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }
}
