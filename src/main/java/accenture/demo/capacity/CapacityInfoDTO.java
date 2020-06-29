package accenture.demo.capacity;

import accenture.demo.user.AppUser;
import java.util.ArrayList;
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
public class CapacityInfoDTO {

  private Integer maxWorkplaceSpace;
  private Integer workspaceCapacityPercentage;
  private Integer maxWorkerAllowedToEnter;
  private Integer workersCurrentlyInOffice;
  private Integer freeSpace;
  private ArrayList<AppUser> employeesInTheBuilding;
}
