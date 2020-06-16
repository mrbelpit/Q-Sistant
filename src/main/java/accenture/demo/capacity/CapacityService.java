package accenture.demo.capacity;

import accenture.demo.exception.appuser.CardIdNotExistException;
import accenture.demo.exception.capacity.CapacitySetupException;
import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.user.AppUser;

public interface CapacityService {

  Message currentStatus(AppUser user);

  Message exitUser(String cardId) throws CardIdNotExistException;

  Message enterUser(String cardId) throws EntryDeniedException, CardIdNotExistException;

  Message capacitySetup(CapacitySetupDTO capacitySetupDTO) throws CapacitySetupException;

  CapacityInfoDTO generalInfo();

  Message register(AppUser user);
}
