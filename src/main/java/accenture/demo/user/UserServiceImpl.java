package accenture.demo.user;

import accenture.demo.exception.RequestBodyIsNullException;
import accenture.demo.exception.login.LoginException;
import accenture.demo.exception.login.NoSuchUserException;
import accenture.demo.exception.login.WrongPasswordException;
import accenture.demo.exception.registration.EmailAddressIsAlreadyRegisteredException;
import accenture.demo.exception.registration.RegistrationException;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public AppUser createNewUser(RegistrationRequestDTO regRequestDTO)
          throws RegistrationException, RequestBodyIsNullException {
    checkIfRegistrationRequestDTOIsNull(regRequestDTO);
    checkIfEmailIsTaken(regRequestDTO);
    AppUser newAppUser = new AppUser(regRequestDTO);
    return userRepository.save(newAppUser);
  }

  private void checkIfRegistrationRequestDTOIsNull(RegistrationRequestDTO regRequestDTO)
          throws RequestBodyIsNullException {
    if (regRequestDTO == null) {
      throw new RequestBodyIsNullException("Please fill in the required fields");
    }
  }

  private void checkIfEmailIsTaken(RegistrationRequestDTO regRequestDTO)
          throws EmailAddressIsAlreadyRegisteredException {
    if (checkIfUserExists(regRequestDTO.getEmail())) {
      throw new EmailAddressIsAlreadyRegisteredException(
              "This email address is already registered");
    }
  }

  private boolean checkIfUserExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  @Override
  public boolean validateLoginCredentials(LoginRequestDTO loginRequestDTO)
          throws LoginException, RequestBodyIsNullException {
    checkIfLoginRequestDTOisNull(loginRequestDTO);
    AppUser userToValidate = new AppUser(loginRequestDTO);
    UserValidationObject userValidationObject =
            validateIfEmailIsRegisteredForAUser(loginRequestDTO.getEmail());
    AppUser storedAppUser = userValidationObject.getAppUser();
    checkEnteredPassword(storedAppUser, userToValidate);
    return true;
  }

  private void checkIfLoginRequestDTOisNull(LoginRequestDTO loginRequestDTO)
          throws RequestBodyIsNullException {
    if (loginRequestDTO == null) {
      throw new RequestBodyIsNullException(
              "Please fill in the required fields");
    }
  }

  private UserValidationObject validateIfEmailIsRegisteredForAUser(String email)
          throws NoSuchUserException {
    UserValidationObject userValidationObject =
            new UserValidationObject(getUserByEmail(email));
    checkIfUserIsValid(userValidationObject);
    return userValidationObject;
  }

  private void checkIfUserIsValid(UserValidationObject userValidationObject)
          throws NoSuchUserException {
    if (userValidationObject.getAppUser() != null) {
      userValidationObject.setValid(true);
    } else {
      throw new NoSuchUserException(
              "This email address in not registered");
    }
  }

  private void checkEnteredPassword(AppUser storedAppUser,
                                    AppUser appUserToValidate)
          throws WrongPasswordException {
    if (!storedAppUser.getPassword().equals(
            appUserToValidate.getPassword())) {
      throw new WrongPasswordException("Wrong password!");
    }
  }

  @Override
  public AppUser getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }
}
