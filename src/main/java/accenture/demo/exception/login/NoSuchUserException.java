package accenture.demo.exception.login;

public class NoSuchUserException extends LoginException {
  public NoSuchUserException(String message) {
    super(message);
  }
}
