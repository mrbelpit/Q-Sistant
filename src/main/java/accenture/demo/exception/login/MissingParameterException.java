package accenture.demo.exception.login;

public class MissingParameterException extends LoginException {
  public MissingParameterException(String message) {
    super(message);
  }
}
