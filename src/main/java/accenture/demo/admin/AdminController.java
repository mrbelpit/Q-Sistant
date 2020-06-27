package accenture.demo.admin;

import accenture.demo.capacity.CapacityService;
import accenture.demo.capacity.CapacitySetupDTO;
import accenture.demo.distance.DistanceService;
import accenture.demo.distance.DistanceSetupDTO;
import accenture.demo.exception.RequestBodyIsNullException;
import accenture.demo.exception.capacity.CapacitySetupException;
import accenture.demo.exception.distance.DistanceException;
import accenture.demo.exception.login.NoSuchUserException;
import accenture.demo.exception.registration.RegistrationException;
import accenture.demo.exception.userfilter.UserFilterIsNotValidException;
import accenture.demo.user.AppUser;
import accenture.demo.user.UserService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private UserService userService;
  private CapacityService capacityService;
  private DistanceService distanceService;

  @Autowired
  public AdminController(UserService userService, CapacityService capacityService,
      DistanceService distanceService) {
    this.userService = userService;
    this.capacityService = capacityService;
    this.distanceService = distanceService;
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

  @GetMapping(value = "/users/{userFilter}")
  public ResponseEntity<?> findUsers(@PathVariable String userFilter)
      throws UserFilterIsNotValidException {
    return new ResponseEntity<>(userService.findUsers(userFilter), HttpStatus.OK);
  }

  @GetMapping("/info")
  public ResponseEntity<?> generalInfo() {
    return new ResponseEntity<>(capacityService.generalInfo(), HttpStatus.OK);
  }

  @GetMapping(value = "/layout", produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> currentOfficeLayout() {
    return new ResponseEntity<>(capacityService.currentLayout(), HttpStatus.OK);
  }

  @PutMapping("/calibrate/headcount")
  public ResponseEntity<?> calibrateCapacity(@RequestBody CapacitySetupDTO capacitySetupDTO)
      throws CapacitySetupException {
    return new ResponseEntity<>(capacityService.capacitySetup(capacitySetupDTO), HttpStatus.OK);
  }

  @PutMapping("/distance")
  public ResponseEntity<?> updateDistance(@Valid @RequestBody DistanceSetupDTO distanceSetupDTO)
      throws DistanceException {
    return new ResponseEntity<>(distanceService.setDistance(distanceSetupDTO), HttpStatus.OK);
  }
}
