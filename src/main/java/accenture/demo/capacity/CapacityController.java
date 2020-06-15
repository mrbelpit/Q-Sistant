package accenture.demo.capacity;

import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.user.AppUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CapacityController {

  @GetMapping("/status")
  public ResponseEntity<?> currentStatus(@RequestBody AppUser user) {
    return new ResponseEntity<>(new Message(CapacityHandler.getInstance().currentPlaceInUserQueue(user)),
        HttpStatus.OK);
  }

  @DeleteMapping("/exit")
  public ResponseEntity<?> exitUser(@RequestBody AppUser user) {
    CapacityHandler.getInstance().exitUser(user);
    return new ResponseEntity<>(new Message("Exit was successful!"), HttpStatus.OK);
  }

  @GetMapping("/entry")
  public ResponseEntity<?> enterUser(@RequestBody AppUser user) throws EntryDeniedException {
    CapacityHandler.getInstance().checkUserAllowed(user);
    return new ResponseEntity<>(new Message("Entry was successful!"), HttpStatus.OK);
  }
}
