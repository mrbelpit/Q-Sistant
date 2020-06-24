package accenture.demo.admin;

import accenture.demo.user.UserRole;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialAppUserRegistrationDTO {

  @NotBlank(message = "First name missing")
  private String firstName;
  @NotBlank(message = "Last name missing")
  private String lastName;
  @NotBlank(message = "Email address missing")
  private String email;
  @NotBlank(message = "Password missing")
  private String password;
  @NotBlank(message = "Card ID missing")
  private String cardId;
  private UserRole userRole;
}
