package accenture.demo.exception.registration;

public class EmailAddressIsAlreadyRegisteredException extends RegistrationException {

  public EmailAddressIsAlreadyRegisteredException(String message) {
    super(message);
  }
}
