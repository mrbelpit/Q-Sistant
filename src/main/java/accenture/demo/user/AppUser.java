package accenture.demo.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import accenture.demo.login.LoginRequestDTO;
import accenture.demo.registration.RegistrationRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String cardId;

  public AppUser(LoginRequestDTO loginRequestDTO){
    this.email = loginRequestDTO.getEmail();
    this.password = loginRequestDTO.getPassword();
  }

  public AppUser(RegistrationRequestDTO regRequestDTO){
    this.firstName = regRequestDTO.getFirstName();
    this.lastName = regRequestDTO.getLastName();
    this.email = regRequestDTO.getEmail();
    this.password = regRequestDTO.getPassword();
  }

}
