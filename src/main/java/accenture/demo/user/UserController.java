package accenture.demo.user;

import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.registration.RegistrationResponseDTO;
import accenture.demo.exception.registration.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {

  private UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(value = "/register")
  public ResponseEntity<?> registerNewUser(
          @Valid @RequestBody(required = false) RegistrationRequestDTO registrationRequestDTO)
          throws RegistrationException {
    User newUser = userService.createNewUser(registrationRequestDTO);
    return ResponseEntity.status(HttpStatus.OK)
            .body(new RegistrationResponseDTO(newUser.getId(), newUser.getFirstName(),
                    newUser.getLastName()
                    , newUser.getEmail(),
                    null, null));
  }
}
