package accenture.demo.user;

import accenture.demo.registration.RegistrationRequestDTO;
import accenture.demo.exception.registration.EmailAddressIsAlreadyRegisteredException;
import accenture.demo.exception.registration.RegistrationException;
import accenture.demo.exception.registration.RequestBodyIsNullException;
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
  public User createNewUser(RegistrationRequestDTO regRequestDTO)
          throws RegistrationException {
    checkIfFieldAreFiled(regRequestDTO);
    if (checkIfUserExists(regRequestDTO.getEmail())) {
      throw new EmailAddressIsAlreadyRegisteredException(
              "This email address is already registered");
    }
    User newUser = new User(regRequestDTO);
    return userRepository.save(newUser);
  }

  private void checkIfFieldAreFiled(RegistrationRequestDTO regRequestDTO)
          throws RequestBodyIsNullException {
    if (regRequestDTO == null) {
      throw new RequestBodyIsNullException("Please fill in the required fields");
    }
  }

  private boolean checkIfUserExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

}
