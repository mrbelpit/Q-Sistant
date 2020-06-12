package accenture.demo.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserValidationObject {
  private boolean isValid = false;
  private AppUser appUser;

  public UserValidationObject(AppUser appUser) {
    this.appUser = appUser;
  }
}
