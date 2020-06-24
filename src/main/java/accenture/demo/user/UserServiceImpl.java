package accenture.demo.user;

import accenture.demo.admin.SpecialAppUserRegistrationDTO;
import accenture.demo.admin.UserFilter;
import accenture.demo.exception.RequestBodyIsNullException;
import accenture.demo.exception.login.LoginException;
import accenture.demo.exception.login.NoSuchUserException;
import accenture.demo.exception.login.WrongPasswordException;
import accenture.demo.exception.registration.EmailAddressIsAlreadyRegisteredException;
import accenture.demo.exception.registration.RegistrationException;
import accenture.demo.exception.userfilter.UserFilterIsNotValidException;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private UserRepository userRepository;
  private ModelMapper modelMapper;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public AppUser createNewUser(RegistrationRequestDTO regRequestDTO)
      throws RegistrationException, RequestBodyIsNullException {
    checkIfRegistrationRequestDTOIsNull(regRequestDTO);
    checkIfEmailIsTaken(regRequestDTO);
    return userRepository.save(new AppUser(regRequestDTO, UserRole.EMPLOYEE));
  }

  @Override
  public List<AppUser> createNewUsers(List<SpecialAppUserRegistrationDTO> regRequestDTOList)
      throws RegistrationException, RequestBodyIsNullException {
    List<AppUser> appUserList = new ArrayList<>();
    for (SpecialAppUserRegistrationDTO regReqDTO : regRequestDTOList) {
      appUserList.add(createNewSpecialUser(regReqDTO));
    }
    return appUserList;
  }

  @Override
  public AppUser createNewSpecialUser(SpecialAppUserRegistrationDTO regRequestDTO)
      throws RegistrationException, RequestBodyIsNullException {
    checkIfRegistrationRequestDTOIsNull(regRequestDTO);
    checkIfEmailIsTaken(regRequestDTO);
    return userRepository.save(new AppUser(regRequestDTO));
  }

  private void checkIfRegistrationRequestDTOIsNull(RegistrationRequestDTO regRequestDTO)
      throws RequestBodyIsNullException {
    if (regRequestDTO == null) {
      throw new RequestBodyIsNullException("Please fill in the required fields");
    }
  }

  private void checkIfRegistrationRequestDTOIsNull(SpecialAppUserRegistrationDTO regRequestDTO)
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

  private void checkIfEmailIsTaken(SpecialAppUserRegistrationDTO regRequestDTO)
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
    AppUser userToValidate = modelMapper.map(loginRequestDTO, AppUser.class);
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

  @Override
  public AppUser findByCardId(String cardId) {
    return userRepository.findByCardId(cardId).orElse(null);
  }

  @Override
  public AppUser deleteUser(Long id) throws NoSuchUserException {
    AppUser appUser = userRepository.findById(id).orElse(null);
    checkIfUserExists(appUser);
    userRepository.delete(appUser);
    return appUser;
  }

  @Override
  public List<AppUser> findUsers(String userFilter) throws UserFilterIsNotValidException {
    UserFilter filter;
    try {
      filter = UserFilter.valueOf(userFilter.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new UserFilterIsNotValidException("User filter is not valid!");
    }
      switch (filter) {
        case ALL:
          return (List<AppUser>) userRepository.findAll();
        case EMPLOYEE:
          return userRepository.findByUserRole(UserRole.EMPLOYEE);
        case ADMIN:
          return userRepository.findByUserRole(UserRole.ADMIN);
        case VIP:
          return userRepository.findByUserRole(UserRole.VIP);
        default:
          return null;
      }
  }

  private void checkIfUserExists(AppUser appUser) throws NoSuchUserException {
    if (appUser == null) {
      throw new NoSuchUserException("User does not exist with the provided Id.");
    }
  }
}
