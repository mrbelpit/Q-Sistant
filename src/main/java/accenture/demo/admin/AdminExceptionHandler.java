package accenture.demo.admin;

import accenture.demo.exception.userfilter.UserFilterIsNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdminExceptionHandler {


  @ExceptionHandler(value = UserFilterIsNotValidException.class)
  public ResponseEntity<?> handleUserFilterIsNotValidException(
      UserFilterIsNotValidException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
