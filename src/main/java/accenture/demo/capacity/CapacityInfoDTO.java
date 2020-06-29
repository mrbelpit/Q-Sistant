package accenture.demo.capacity;

import accenture.demo.user.AppUserDTO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CapacityInfoDTO {

  private Integer maxWorkplaceSpace;
  private Integer workspaceCapacityPercentage;
  private Integer maxWorkerAllowedToEnter;
  private Integer workersCurrentlyInOffice;
  private Integer freeSpace;
  private List<AppUserDTO> employeesInTheBuilding;
}
