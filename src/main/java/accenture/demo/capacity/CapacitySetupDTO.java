package accenture.demo.capacity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CapacitySetupDTO {

  private CapacityModifier modifier;
  private Integer value;
}
