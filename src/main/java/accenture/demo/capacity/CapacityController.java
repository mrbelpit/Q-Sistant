package accenture.demo.capacity;

import accenture.demo.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CapacityController {

  @GetMapping("/status")
  public ResponseEntity<?> currentStatus(@RequestBody User user){
    return new ResponseEntity<>(CapacityHandler.currentPlaceInUserQueue(user), HttpStatus.OK);
  }
}
