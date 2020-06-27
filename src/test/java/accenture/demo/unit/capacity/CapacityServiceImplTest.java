package accenture.demo.unit.capacity;

import static org.mockito.Mockito.when;

import accenture.demo.capacity.CapacityHandler;
import accenture.demo.capacity.CapacityInfoDTO;
import accenture.demo.capacity.CapacityModifier;
import accenture.demo.capacity.CapacityServiceImpl;
import accenture.demo.capacity.CapacitySetupDTO;
import accenture.demo.capacity.Message;
import accenture.demo.capacity.QueueNotificationSetupDTO;
import accenture.demo.exception.QueueNotificationNumberNotValidException;
import accenture.demo.exception.appuser.CardIdNotExistException;
import accenture.demo.exception.capacity.CapacitySetupException;
import accenture.demo.exception.capacity.InvalidCapacitySetupModifierException;
import accenture.demo.exception.capacity.InvalidCapacitySetupValueException;
import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.user.AppUser;
import accenture.demo.user.UserRole;
import accenture.demo.user.UserService;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CapacityServiceImplTest {

  private CapacityServiceImpl capacityService;

  @Mock
  private UserService userService;

  AppUser appUser;
  @Before
  public void setup() {
    capacityService = new CapacityServiceImpl(userService);
    setupCapacityHandler(10, 10);
    CapacityHandler.getInstance().setUsersCurrentlyInOffice(new ArrayList<>());
    appUser = new AppUser(1L, "asd", "asd", "asd", "asd", null, UserRole.EMPLOYEE);
  }

  @Test
  public void register_oneAppUserWithFreeOfficePlace_assertsTrue() {
    String msg = "You can enter the office!";
    Assert.assertEquals(msg, capacityService.register(new AppUser()).getMessage());
  }

  @Test
  public void register_oneAppUserWithNoFreeOfficePlace_assertsTrue() {
    capacityService.register(new AppUser());
    String msg = "Your current place in the queue " + 1 + "!";
    Assert.assertEquals(msg, capacityService.register(new AppUser()).getMessage());
  }

  @Test
  public void currentStatus_withRegisteredAllowedAppUser_assertsTrue() {
    AppUser user = new AppUser();
    capacityService.register(user);
    String msg = "You can enter the office today!";
    Assert.assertEquals(msg, capacityService.currentStatus(user).getMessage());
  }

  @Test
  public void currentStatus_withRegisteredQueueAppUser_assertsTrue() {
    capacityService.register(new AppUser());
    AppUser user = new AppUser();
    capacityService.register(user);
    String msg = "Your current place in the queue is " + 1 + "!";
    Assert.assertEquals(msg, capacityService.currentStatus(user).getMessage());
  }

  @Test
  public void currentStatus_withoutRegisterAppUser_assertsTrue() {
    String msg = "You have not applied to enter the office today!";
    Assert.assertEquals(msg, capacityService.currentStatus(new AppUser()).getMessage());
  }

  @Test(expected = CapacitySetupException.class)
  public void capacitySetup_withNull_assertsCapacitySetupException()
      throws CapacitySetupException {
    capacityService.capacitySetup(null);
  }

  @Test(expected = InvalidCapacitySetupModifierException.class)
  public void capacitySetup_withNullModifier_assertsInvalidCapacitySetupModifierException()
      throws CapacitySetupException {
    capacityService.capacitySetup(new CapacitySetupDTO(null, 10));
  }

  @Test(expected = InvalidCapacitySetupValueException.class)
  public void capacitySetup_withNullValue_assertsInvalidCapacitySetupValueException()
      throws CapacitySetupException {
    capacityService.capacitySetup(new CapacitySetupDTO(CapacityModifier.WORKPLACE_SPACE, null));
  }

  @Test(expected = InvalidCapacitySetupValueException.class)
  public void capacitySetup_withZeroValue_assertsInvalidCapacitySetupValueException()
      throws CapacitySetupException {
    capacityService.capacitySetup(new CapacitySetupDTO(CapacityModifier.WORKSPACE_CAPACITY, 0));
  }

  @Test(expected = InvalidCapacitySetupValueException.class)
  public void capacitySetup_withNegativeValue_assertsInvalidCapacitySetupValueException()
      throws CapacitySetupException {
    capacityService.capacitySetup(new CapacitySetupDTO(CapacityModifier.WORKSPACE_CAPACITY, -5));
  }

  @Test
  public void capacitySetup_withCapacityModifierAndIncreasedValue_assertsTrue()
      throws CapacitySetupException {
    int percentage = 20;
    Message message = capacityService
        .capacitySetup(new CapacitySetupDTO(CapacityModifier.WORKSPACE_CAPACITY, percentage));
    String expectedMsg = "The max workplace capacity successfully set to " + percentage
        + ". It is valid from now.";
    Assert.assertEquals(expectedMsg, message.getMessage());
    createAppUsers(3);

    CapacityHandler capacityHandler = CapacityHandler.getInstance();

    Assert.assertEquals(0, capacityHandler.getAllowedUsers().remainingCapacity());
    Assert.assertEquals(1, capacityHandler.getUserQueue().size());
  }

  @Test
  public void capacitySetup_withCapacityModifierAndDecreasedValue_assertsTrue()
      throws CapacitySetupException {
    int percentage = 5;
    Message message = capacityService
        .capacitySetup(new CapacitySetupDTO(CapacityModifier.WORKSPACE_CAPACITY, percentage));
    String expectedMsg = "The max workplace capacity successfully set to " + percentage
        + ". It is valid from tomorrow.";
    Assert.assertEquals(expectedMsg, message.getMessage());
    createAppUsers(3);

    CapacityHandler capacityHandler = CapacityHandler.getInstance();

    Assert.assertEquals(0, capacityHandler.getAllowedUsers().remainingCapacity());
    Assert.assertEquals(2, capacityHandler.getUserQueue().size());
  }

  @Test
  public void capacitySetup_withSpaceModifierAndIncreasedValue_assertsTrue()
      throws CapacitySetupException {
    int percentage = 300;
    Message message = capacityService
        .capacitySetup(new CapacitySetupDTO(CapacityModifier.WORKPLACE_SPACE, percentage));
    String expectedMsg = "The max workplace space place successfully set to " + percentage
        + ". It is valid from tomorrow.";
    Assert.assertEquals(expectedMsg, message.getMessage());
    createAppUsers(3);

    CapacityHandler capacityHandler = CapacityHandler.getInstance();

    Assert.assertEquals(0, capacityHandler.getAllowedUsers().remainingCapacity());
    Assert.assertEquals(2, capacityHandler.getUserQueue().size());
  }

  @Test
  public void capacitySetup_withSpaceModifierAndDecreasedValue_assertsTrue()
      throws CapacitySetupException {
    int percentage = 200;
    Message message = capacityService
        .capacitySetup(new CapacitySetupDTO(CapacityModifier.WORKPLACE_SPACE, percentage));
    String expectedMsg = "The max workplace space place successfully set to " + percentage
        + ". It is valid from tomorrow.";
    Assert.assertEquals(expectedMsg, message.getMessage());
    createAppUsers(3);

    CapacityHandler capacityHandler = CapacityHandler.getInstance();

    Assert.assertEquals(0, capacityHandler.getAllowedUsers().remainingCapacity());
    Assert.assertEquals(2, capacityHandler.getUserQueue().size());
  }

  @Test(expected = EntryDeniedException.class)
  public void enterUser_withNoPlaceInOffice_assertsEntryDeniedException()
      throws EntryDeniedException, CardIdNotExistException {
    String cardId = "cardId";
    appUser.setCardId(cardId);
    when(userService.findByCardId(cardId)).thenReturn(appUser);
    createAppUsers(1);
    capacityService.enterUser(cardId);
  }

  @Test
  public void enterUser_withRegisteredPlaceInOffice_assertsEqual()
      throws EntryDeniedException, CardIdNotExistException {
    String cardId = "cardId";
    appUser.setCardId(cardId);
    when(userService.findByCardId(cardId)).thenReturn(appUser);
    capacityService.register(appUser);
    Assert.assertEquals("Entry was successful!", capacityService.enterUser(cardId).getMessage());
  }

  @Test
  public void enterUser_withNoRegistration_assertsEqual()
      throws EntryDeniedException, CardIdNotExistException {
    String cardId = "cardId";
    appUser.setCardId(cardId);
    when(userService.findByCardId(cardId)).thenReturn(appUser);
    Assert.assertEquals("Entry was successful!",
        capacityService.enterUser(cardId).getMessage());
  }

  @Test
  public void exitUser_withRegister_assertsEqual() throws CardIdNotExistException {
    String cardId = "cardId";
    appUser.setCardId(cardId);
    when(userService.findByCardId(cardId)).thenReturn(appUser);
    capacityService.register(appUser);
    createAppUsers(1);
    CapacityHandler capacityHandler = CapacityHandler.getInstance();

    Assert.assertEquals(0, capacityHandler.getAllowedUsers().remainingCapacity());
    Assert.assertEquals(1, capacityHandler.getUserQueue().size());
    Assert.assertEquals("Exit was successful!", capacityService.exitUser(cardId).getMessage());
    Assert.assertEquals(0, capacityHandler.getAllowedUsers().remainingCapacity());
    Assert.assertEquals(0, capacityHandler.getUserQueue().size());
  }

  @Test
  public void exitUser_withEnterUser_assertsEqual()
      throws EntryDeniedException, CardIdNotExistException {
    String cardId = "cardId";
    appUser.setCardId(cardId);
    when(userService.findByCardId(cardId)).thenReturn(appUser);
    capacityService.enterUser(cardId);
    createAppUsers(1);
    CapacityHandler capacityHandler = CapacityHandler.getInstance();

    Assert.assertEquals(0, capacityHandler.getAllowedUsers().remainingCapacity());
    Assert.assertEquals(1, capacityHandler.getUserQueue().size());
    Assert.assertEquals("Exit was successful!", capacityService.exitUser(cardId).getMessage());
    Assert.assertEquals(0, capacityHandler.getAllowedUsers().remainingCapacity());
    Assert.assertEquals(0, capacityHandler.getUserQueue().size());
  }

  @Test
  public void generalInfo_assertsEqual() throws EntryDeniedException, CardIdNotExistException {
    String cardId = "cardId";
    appUser.setCardId(cardId);
    when(userService.findByCardId(cardId)).thenReturn(appUser);
    capacityService.enterUser(cardId);
    capacityService.register(new AppUser());
    CapacityInfoDTO capacityInfoDTO = capacityService.generalInfo();
    Assert.assertEquals((Integer) 10, capacityInfoDTO.getMaxWorkplaceSpace());
    Assert.assertEquals((Integer) 10, capacityInfoDTO.getWorkspaceCapacityPercentage());
    Assert.assertEquals((Integer) 1, capacityInfoDTO.getMaxWorkerAllowedToEnter());
    Assert.assertEquals((Integer) 1, capacityInfoDTO.getWorkersCurrentlyInOffice());
    Assert.assertEquals((Integer) 0, capacityInfoDTO.getFreeSpace());
    Assert.assertEquals(new ArrayList<>(Collections.singletonList(appUser)),
        capacityInfoDTO.getEmployeesInTheBuilding());
  }

  @Test(expected = CardIdNotExistException.class)
  public void enterUser_withNotValidCardId_assertsCardIdNotExistException()
      throws EntryDeniedException, CardIdNotExistException {
    String cardId = "cardId";
    when(userService.findByCardId(cardId)).thenReturn(null);
    capacityService.enterUser(cardId);
  }

  @Test(expected = CardIdNotExistException.class)
  public void exitUser_withNotValidCardId_assertsCardIdNotExistException()
      throws EntryDeniedException, CardIdNotExistException {
    String cardId = "cardId";
    when(userService.findByCardId(cardId)).thenReturn(null);
    capacityService.enterUser(cardId);
  }

  @Test
  public void setNumberToSendNotification_withValidValue_assertsEqual()
      throws QueueNotificationNumberNotValidException {
 Message message = capacityService.setNumberToSendNotification(new QueueNotificationSetupDTO(4));
  Assert.assertEquals("Notification number successfully set to 4!",message.getMessage());
  }

  @Test(expected = QueueNotificationNumberNotValidException.class)
  public void setNumberToSendNotification_withZeroValue_assertsEqual()
      throws QueueNotificationNumberNotValidException {
    capacityService.setNumberToSendNotification(new QueueNotificationSetupDTO(0));
  }

  @Test(expected = QueueNotificationNumberNotValidException.class)
  public void setNumberToSendNotification_withNegativeValue_assertsEqual()
      throws QueueNotificationNumberNotValidException {
    capacityService.setNumberToSendNotification(new QueueNotificationSetupDTO(-5));
  }



  private void createAppUsers(int amount) {
    for (int i = 0; i < amount; i++) {
      capacityService.register(new AppUser());
    }
  }

  private void setupCapacityHandler(Integer maxWorkplaceSpace, Integer workspaceCapacity) {
    CapacityHandler.getInstance().setMaxWorkplaceSpace(maxWorkplaceSpace);
    CapacityHandler.getInstance().setWorkspaceCapacity(workspaceCapacity);
    CapacityHandler.getInstance().restartDay();
  }
}
