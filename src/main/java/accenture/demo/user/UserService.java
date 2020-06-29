package accenture.demo.user;

import accenture.demo.admin.SpecialAppUserRegistrationDTO;
import accenture.demo.exception.RequestBodyIsNullException;
import accenture.demo.exception.login.LoginException;
import accenture.demo.exception.login.NoSuchUserException;
import accenture.demo.exception.registration.RegistrationException;
import accenture.demo.exception.userfilter.UserFilterIsNotValidException;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import java.util.List;

public interface UserService {

  AppUser createNewUser(RegistrationRequestDTO regRequestDTO)
      throws RegistrationException, RequestBodyIsNullException;

  List<AppUser> createNewUsers(List<SpecialAppUserRegistrationDTO> regRequestDTOList)
      throws RegistrationException, RequestBodyIsNullException;

  AppUser createNewSpecialUser(SpecialAppUserRegistrationDTO regRequestDTO)
      throws RegistrationException, RequestBodyIsNullException;

  boolean validateLoginCredentials(LoginRequestDTO loginRequestDTO)
      throws LoginException, RequestBodyIsNullException;

  AppUser getUserByEmail(String email);

  AppUser findByCardId(String cardId);

  AppUser deleteUser(Long id) throws NoSuchUserException;

  List<AppUserDTO> findUsers(String userFilter)
      throws UserFilterIsNotValidException;
}
