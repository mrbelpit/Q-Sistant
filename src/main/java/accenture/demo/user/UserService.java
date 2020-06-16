package accenture.demo.user;

import accenture.demo.exception.RequestBodyIsNullException;
import accenture.demo.exception.login.LoginException;
import accenture.demo.exception.registration.RegistrationException;
import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;

public interface UserService {

  AppUser createNewUser(RegistrationRequestDTO regRequestDTO)
      throws RegistrationException, RequestBodyIsNullException;

  boolean validateLoginCredentials(LoginRequestDTO loginRequestDTO)
      throws LoginException, RequestBodyIsNullException;

  AppUser getUserByEmail(String email);

  AppUser findByCardId(String cardId);

}
