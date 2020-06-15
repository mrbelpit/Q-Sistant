package accenture.demo.exception.login;

public class WrongPasswordException extends LoginException {

  public WrongPasswordException(String message) {
    super(message);
  }
}
