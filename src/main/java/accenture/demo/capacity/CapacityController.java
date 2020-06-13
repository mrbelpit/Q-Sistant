package accenture.demo.capacity;

import accenture.demo.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CapacityController {

  @GetMapping("/status")
  public ResponseEntity<?> currentStatus(@RequestBody User user) {
    return new ResponseEntity<>(new Message(CapacityHandler.currentPlaceInUserQueue(user)),
        HttpStatus.OK);
  }

  @DeleteMapping("/exit")
  public ResponseEntity<?> exitUser(@RequestBody User user) {
    CapacityHandler.exitUser(user);
    return new ResponseEntity<>(new Message("Exit was successful!"), HttpStatus.OK);
  }
}
