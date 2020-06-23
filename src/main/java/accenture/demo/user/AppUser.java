package accenture.demo.user;

import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String cardId;
  private UserRole userRole;

  public AppUser(LoginRequestDTO loginRequestDTO) {
    this.email = loginRequestDTO.getEmail();
    this.password = loginRequestDTO.getPassword();
  }

  public AppUser(RegistrationRequestDTO registrationRequestDTO, UserRole userRole) {
    this.firstName = registrationRequestDTO.getFirstName();
    this.lastName = registrationRequestDTO.getLastName();
    this.email = registrationRequestDTO.getEmail();
    this.password = registrationRequestDTO.getPassword();
    this.cardId = registrationRequestDTO.getCardId();
    this.userRole = userRole;

  }
}
