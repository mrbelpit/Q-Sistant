package accenture.demo.registration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDTO {

  @NotBlank(message = "First name missing")
  private String firstName;
  @NotBlank(message = "Last name missing")
  private String lastName;
  @NotBlank(message = "Email address missing")
  private String email;
  @NotBlank(message = "Password missing")
  private String password;
  @NotBlank(message = "Card ID missing")
  public String cardId;
}
