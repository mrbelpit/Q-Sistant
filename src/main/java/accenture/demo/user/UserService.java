package accenture.demo.user;

import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.exception.registration.EmailAddressIsAlreadyRegistered;
import accenture.demo.exception.registration.RegistrationException;

public interface UserService {

  User createNewUser(RegistrationRequestDTO regRequestDTO)
          throws RegistrationException;

}
