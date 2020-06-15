package accenture.demo.user;

import accenture.demo.exception.RequestBodyIsNullException;
import accenture.demo.exception.login.LoginException;
import accenture.demo.exception.login.NoSuchUserException;
import accenture.demo.exception.login.WrongPasswordException;
import accenture.demo.exception.registration.EmailAddressIsAlreadyRegisteredException;
import accenture.demo.exception.registration.RegistrationException;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

  private UserService userService;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private UserRepository userRepository;

  @Before
  public void setup() {
    this.userService = new UserServiceImpl(userRepository);
  }

  private AppUser testAppUser = new AppUser(1L, "John", "Doe", "jd@email.com", "pw", "1");

  private RegistrationRequestDTO regRequestDTO =
          new RegistrationRequestDTO("John", "Doe", "jd@email.com",
                  "pw");

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void createNewUser_registrationSuccess()
          throws RegistrationException, RequestBodyIsNullException {
    when(userRepository.save(any())).thenReturn(testAppUser);
    assertEquals(testAppUser, userService.createNewUser(regRequestDTO));
  }

  @Test(expected = EmailAddressIsAlreadyRegisteredException.class)
  public void createNewUser_emailAlreadyTaken()
          throws RegistrationException, RequestBodyIsNullException {
    when(userRepository.findByEmail(any())).thenReturn(Optional.of(testAppUser));
    userService.createNewUser(regRequestDTO);
  }

  @Test
  public void createNewUser_MissingRequestBody()
          throws RegistrationException, RequestBodyIsNullException {
    when(userRepository.save(any())).thenReturn(null);
    thrown.expect(RequestBodyIsNullException.class);
    thrown.expectMessage("Please fill in the required fields");
    userService.createNewUser(null);
  }

  @Test
  public void login_success() throws LoginException, RequestBodyIsNullException {
    when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(testAppUser));
    userService.validateLoginCredentials(new LoginRequestDTO("jd@email.com", "pw"));
  }

  @Test
  public void validateLoginCredentials_DTOisNull()
          throws LoginException, RequestBodyIsNullException {
    when(userRepository.findByEmail(any())).thenReturn(null);
    thrown.expect(RequestBodyIsNullException.class);
    thrown.expectMessage("Please fill in the required fields");
    userService.validateLoginCredentials(null);
  }

  @Test
  public void validateLoginCredentials_EmailIsNotRegistered()
          throws LoginException, RequestBodyIsNullException {
    when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(null));
    thrown.expect(NoSuchUserException.class);
    thrown.expectMessage("This email address in not registered");
    userService.validateLoginCredentials(new LoginRequestDTO("jd@email.com", "pw"));
  }

  @Test
  public void validateLoginCredentials_InvalidPassword()
          throws LoginException, RequestBodyIsNullException {
    AppUser storedUser = new AppUser(1L, "John", "Doe", "jd@email.com", "pw", "1");
    when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(storedUser));
    thrown.expect(WrongPasswordException.class);
    thrown.expectMessage("Wrong password!");
    userService.validateLoginCredentials(new LoginRequestDTO("jd@email.com", "invalid"));
  }
}
