package accenture.demo.capacity;

import accenture.demo.exception.appuser.CardIdNotExistException;
import accenture.demo.exception.capacity.CapacitySetupException;
import accenture.demo.exception.capacity.InvalidCapacitySetupModifierException;
import accenture.demo.exception.capacity.InvalidCapacitySetupValueException;
import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.user.AppUser;
import accenture.demo.user.UserService;
import java.util.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CapacityServiceImpl implements CapacityService {

  private UserService userService;

  @Autowired
  public CapacityServiceImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Message currentStatus(AppUser user) {
    Queue<AppUser> userQueue = CapacityHandler.getInstance().getUserQueue();
    for (int i = 0; i < userQueue.size(); i++) {
      if (userQueue.toArray()[i] == user) {
        return new Message("Your current place in the queue is " + (i + 1) + "!");
      }
    }
    if (CapacityHandler.getInstance().getAllowedUsers().contains(user)) {
      return new Message("You can enter the office today!");
    }
    return new Message("You have not applied to enter the office today!");
  }

  @Override
  public Message exitUser(String cardId) throws CardIdNotExistException {
    AppUser user = userService.findByCardId(cardId);
    checkCardId(user);
    CapacityHandler.getInstance().exitUser(user);
    return new Message("Exit was successful!");
  }

  @Override
  public Message enterUser(String cardId) throws EntryDeniedException, CardIdNotExistException {
    AppUser user = userService.findByCardId(cardId);
    checkCardId(user);
    if (CapacityHandler.getInstance().enterUser(user)) {
      return new Message("Entry was successful!");
    } else {
      throw new EntryDeniedException("User is currently not allowed to enter!");
    }
  }

  @Override
  public Message capacitySetup(CapacitySetupDTO capacitySetupDTO) throws CapacitySetupException {

    checkCapacitySetupDTO(capacitySetupDTO);

    if (capacitySetupDTO.getModifier().equals(CapacityModifier.WORKPLACE_SPACE)) {
      CapacityHandler.getInstance().setMaxWorkplaceSpace(capacitySetupDTO.getValue());
      return new Message(
          "The max workplace space place successfully set to " + CapacityHandler.getInstance()
              .getMaxWorkplaceSpace() + ". It is valid from tomorrow.");
    }

    if (capacitySetupDTO.getModifier().equals(CapacityModifier.WORKSPACE_CAPACITY)
        && capacitySetupDTO.getValue() < percentageChanger()) {
      CapacityHandler.getInstance().setWorkspaceCapacity(capacitySetupDTO.getValue());
      return new Message(
          "The max workplace capacity successfully set to " + percentageChanger()
              + ". It is valid from tomorrow.");
    }

    if (capacitySetupDTO.getModifier().equals(CapacityModifier.WORKSPACE_CAPACITY)
        && capacitySetupDTO.getValue() > percentageChanger()) {
      CapacityHandler.getInstance().increaseWorkspaceCapacity(capacitySetupDTO.getValue());
      return new Message(
          "The max workplace capacity successfully set to " + percentageChanger()
              + ". It is valid from now.");
    }
    return null;
  }

  @Override
  public CapacityInfoDTO generalInfo() {
    CapacityHandler capacityHandler = CapacityHandler.getInstance();
    Integer maxWorkerAllowedToEnter = (int) (capacityHandler.getMaxWorkplaceSpace() * capacityHandler.getWorkspaceCapacity());
    return new CapacityInfoDTO(
        capacityHandler.getMaxWorkplaceSpace(),
        percentageChanger(),
        maxWorkerAllowedToEnter,
        capacityHandler.getUsersCurrentlyInOffice().size(),
        capacityHandler.getAllowedUsers().remainingCapacity(),
        capacityHandler.getUsersCurrentlyInOffice());
  }

  @Override
  public Message register(AppUser user) {
    return new Message(CapacityHandler.getInstance().registerAppUser(user));
  }

  private void checkCapacitySetupDTO(CapacitySetupDTO capacitySetupDTO)
      throws CapacitySetupException {
    if (capacitySetupDTO == null) {
      throw new CapacitySetupException("CapacitySetupDTO can not be null!");
    }

    if (capacitySetupDTO.getModifier() == null) {
      throw new InvalidCapacitySetupModifierException("The provided modifier can not be null!");
    }

    if (capacitySetupDTO.getValue() == null) {
      throw new InvalidCapacitySetupValueException("The provided value can not be null!");
    }

    if (!capacitySetupDTO.getModifier().equals(CapacityModifier.WORKPLACE_SPACE)
        && !capacitySetupDTO.getModifier().equals(CapacityModifier.WORKSPACE_CAPACITY)) {
      throw new InvalidCapacitySetupModifierException("The provided modifier is invalid!");
    }

    if (capacitySetupDTO.getValue() <= 0) {
      throw new InvalidCapacitySetupValueException("The provided value can not be less than 1!");
    }
  }

  private Integer percentageChanger() {
    return (int) (CapacityHandler.getInstance().getWorkspaceCapacity() * 100);
  }

  private void checkCardId(AppUser user) throws CardIdNotExistException {
    if (user == null){
      throw new CardIdNotExistException("The provided card ID is not valid!");
    }
  }

  public AppUser getNthUserInQueue(int n){
    return CapacityHandler.getInstance().getNthUserInQueue(n);
  }

  public byte[] currentLayout() {
    return CapacityHandler.getInstance().getCurrentLayoutImage();
  }

  public byte[] getAssignedStationImage(AppUser user) {
    return CapacityHandler.getInstance().getAssignedStationImage(user);
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void restartDay() {
    CapacityHandler.getInstance().restartDay();
  }
}
