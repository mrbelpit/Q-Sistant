package accenture.demo.user;

import accenture.demo.exception.registration.RegistrationException;
import accenture.demo.registration.RegistrationRequestDTO;

public interface UserService {

  User createNewUser(RegistrationRequestDTO regRequestDTO)
          throws RegistrationException;

}
