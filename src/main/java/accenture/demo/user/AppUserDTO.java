package accenture.demo.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AppUserDTO {

  private String firstName;
  private String lastName;
  private String email;
  private String cardId;
  private UserRole userRole;
}
