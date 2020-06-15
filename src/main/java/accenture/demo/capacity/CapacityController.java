package accenture.demo.capacity;

import accenture.demo.exception.capacity.CapacitySetupException;
import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/office")
public class CapacityController {

  private CapacityService capacityService;

  @Autowired
  public CapacityController(CapacityService capacityService) {
    this.capacityService = capacityService;
  }

  @PutMapping("/register")
  public ResponseEntity<?> registerPlace(@RequestBody AppUser user) {
    return new ResponseEntity<>(capacityService.register(user), HttpStatus.OK);
  }

  @GetMapping("/status")
  public ResponseEntity<?> currentStatus(@RequestBody AppUser user) {
    return new ResponseEntity<>(capacityService.currentStatus(user), HttpStatus.OK);
  }

  @GetMapping("/entry")
  public ResponseEntity<?> enterUser(@RequestBody AppUser user) throws EntryDeniedException {
    return new ResponseEntity<>(capacityService.enterUser(user), HttpStatus.OK);
  }

  @DeleteMapping("/exit")
  public ResponseEntity<?> exitUser(@RequestBody AppUser user) {

    return new ResponseEntity<>(capacityService.exitUser(user), HttpStatus.OK);
  }

  @PutMapping("/admin/calibrate")
  public ResponseEntity<?> calibrateCapacity(@RequestBody CapacitySetupDTO capacitySetupDTO)
      throws CapacitySetupException {
    return new ResponseEntity<>(capacityService.capacitySetup(capacitySetupDTO), HttpStatus.OK);
  }

  @GetMapping("/admin/info")
  public ResponseEntity<?> generalInfo() {
    return new ResponseEntity<>(capacityService.generalInfo(), HttpStatus.OK);
  }
}
