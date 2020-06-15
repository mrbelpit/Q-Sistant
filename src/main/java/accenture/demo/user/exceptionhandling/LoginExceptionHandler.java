package accenture.demo.user.exceptionhandling;

import accenture.demo.exception.login.NoSuchUserException;
import accenture.demo.exception.login.WrongPasswordException;
import accenture.demo.login.LoginResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LoginExceptionHandler {


  @ExceptionHandler(value = NoSuchUserException.class)
  public ResponseEntity<LoginResponseDTO> handleNoSuchUserException(
          NoSuchUserException e) {
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new LoginResponseDTO("error", null,
                    e.getMessage()));
  }

  @ExceptionHandler(value = WrongPasswordException.class)
  public ResponseEntity<LoginResponseDTO> handleWrongPasswordException(
          WrongPasswordException e) {
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new LoginResponseDTO("error", null,
                    e.getMessage()));
  }
}