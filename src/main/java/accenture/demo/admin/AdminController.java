package accenture.demo.admin;

import accenture.demo.exception.RequestBodyIsNullException;
import accenture.demo.exception.login.NoSuchUserException;
import accenture.demo.exception.registration.RegistrationException;
import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.user.AppUser;
import accenture.demo.user.UserRole;
import accenture.demo.user.UserService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private UserService userService;

  @Autowired
  public AdminController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(value = "/user/register")
  public ResponseEntity<?> registerNewVipUser(
      @Valid @RequestBody(required = false) SpecialAppUserRegistrationDTO specAppUserRegDTO)
      throws RegistrationException, RequestBodyIsNullException {
    AppUser newAppUser = userService.createNewSpecialUser(specAppUserRegDTO);
    return new ResponseEntity<>(newAppUser, HttpStatus.OK);
  }

  @PostMapping(value = "/users/register")
  public ResponseEntity<?> registerNewVipUser(
      @Valid @RequestBody(required = false) List<SpecialAppUserRegistrationDTO> specAppUserRegDTOList)
      throws RegistrationException, RequestBodyIsNullException {
    List<AppUser> newAppUsers = userService
        .createNewUsers(specAppUserRegDTOList);
    return new ResponseEntity<>(newAppUsers, HttpStatus.OK);
  }

  @DeleteMapping(value = "/users/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) throws NoSuchUserException {
    return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
  }
}
