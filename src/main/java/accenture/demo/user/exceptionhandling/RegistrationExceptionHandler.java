package accenture.demo.user.exceptionhandling;


import accenture.demo.exception.registration.EmailAddressIsAlreadyRegisteredException;
import accenture.demo.registration.RegistrationResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RegistrationExceptionHandler {

  @ExceptionHandler(value = EmailAddressIsAlreadyRegisteredException.class)
  public ResponseEntity<?> emailAddressIsAlreadyRegistered(
          EmailAddressIsAlreadyRegisteredException ex) {
    String message = ex.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new RegistrationResponseDTO(null, null, null,
                    null, "error", message));
  }
}
