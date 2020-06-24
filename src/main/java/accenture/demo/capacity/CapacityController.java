package accenture.demo.capacity;

import accenture.demo.exception.appuser.CardIdNotExistException;
import accenture.demo.exception.capacity.CapacitySetupException;
import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.user.AppUser;
import accenture.demo.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/office")
public class CapacityController {

  private CapacityService capacityService;
  private UserService userService;

  @Autowired
  public CapacityController(CapacityService capacityService, UserService userService) {
    this.capacityService = capacityService;
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerPlace() {
    return new ResponseEntity<>(capacityService.register(extractUserFromToken()), HttpStatus.OK);
  }

  @GetMapping("/status")
  public ResponseEntity<?> currentStatus() {
    return new ResponseEntity<>(capacityService.currentStatus(extractUserFromToken()), HttpStatus.OK);
  }

  @PostMapping("/entry/{cardId}")
  public ResponseEntity<?> enterUser(@PathVariable String cardId)
      throws EntryDeniedException, CardIdNotExistException {
    return new ResponseEntity<>(capacityService.enterUser(cardId), HttpStatus.OK);
  }

  @DeleteMapping("/exit/{cardId}")
  public ResponseEntity<?> exitUser(@PathVariable String cardId) throws CardIdNotExistException {
    return new ResponseEntity<>(capacityService.exitUser(cardId), HttpStatus.OK);
  }

  private AppUser extractUserFromToken(){
    String appUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    return userService.getUserByEmail(appUserEmail);
  }
}
