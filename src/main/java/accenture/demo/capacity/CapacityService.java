package accenture.demo.capacity;

import accenture.demo.exception.capacity.CapacitySetupException;
import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.user.AppUser;

public interface CapacityService {

  Message currentStatus(AppUser user);

  Message exitUser(AppUser user);

  Message enterUser(AppUser user) throws EntryDeniedException;

  Message capacitySetup(CapacitySetupDTO capacitySetupDTO) throws CapacitySetupException;

  CapacityInfoDTO generalInfo();

  Message register(AppUser user);
}
