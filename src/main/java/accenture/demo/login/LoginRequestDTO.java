package accenture.demo.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
  @NotBlank(message = "Email address is missing")
  private String email;
  @NotBlank(message = "Password is missing")
  private String password;
}
