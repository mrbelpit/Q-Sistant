package accenture.demo.user;

import accenture.demo.exception.RequestBodyIsNullException;
import accenture.demo.exception.login.LoginException;
import accenture.demo.exception.registration.RegistrationException;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.login.LoginResponseDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.registration.RegistrationResponseDTO;
import accenture.demo.security.CustomUserDetailService;
import accenture.demo.security.JwtUtility;
import org.modelmapper.ModelMapper;
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
  private CustomUserDetailService userDetailsService;
  private JwtUtility jwtTokenUtil;
  private ModelMapper modelMapper;

  @Autowired
  public UserController(UserService userService, CustomUserDetailService userDetailsService,
                        JwtUtility jwtTokenUtil, ModelMapper modelMapper) {
    this.userService = userService;
    this.userDetailsService = userDetailsService;
    this.jwtTokenUtil = jwtTokenUtil;
    this.modelMapper = modelMapper;
  }

  @PostMapping(value = "/register")
  public ResponseEntity<?> registerNewUser(
          @Valid @RequestBody(required = false) RegistrationRequestDTO registrationRequestDTO)
          throws RegistrationException, RequestBodyIsNullException {
    AppUser newAppUser = userService.createNewUser(registrationRequestDTO);
    return new ResponseEntity<>(modelMapper.map(newAppUser, RegistrationResponseDTO.class),HttpStatus.OK);
  }

  @PostMapping(value = "/login")
  public ResponseEntity<?> login(
          @Valid @RequestBody(required = false) LoginRequestDTO loginRequestDTO)
          throws LoginException, RequestBodyIsNullException {
    userService.validateLoginCredentials(loginRequestDTO);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(new LoginResponseDTO("ok",
                    jwtTokenUtil.generateToken(
                            userDetailsService.loadUserByUsername(
                                    loginRequestDTO.getEmail())),
                    null));
  }
}
