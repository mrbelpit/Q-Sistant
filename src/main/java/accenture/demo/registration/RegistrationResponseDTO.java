package accenture.demo.registration;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationResponseDTO {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String status;
  private String message;
}
