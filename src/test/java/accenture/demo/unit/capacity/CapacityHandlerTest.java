package accenture.demo.unit.capacity;

import accenture.demo.capacity.CapacityHandler;
import accenture.demo.user.AppUser;
import accenture.demo.user.UserRole;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CapacityHandlerTest {

  @Before
  public void setup() {
    setupCapacityHandler(250, 10);
    CapacityHandler.getInstance().setUsersCurrentlyInOffice(new ArrayList<>());
    CapacityHandler.getInstance().setAssignedStations(new HashMap<>());
  }

  @Test
  public void addUser_expectsEqual() {
    CapacityHandler.getInstance().registerAppUser(new AppUser());
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getNumberOfAssingedStations());
  }

  @Test
  public void addUser_withElevenUser_expectsEqual() {
    setupCapacityHandler(100, 10);

    for (int i = 0; i < 11; i++) {
      AppUser user = new AppUser();
      user.setId((long)i);
      CapacityHandler.getInstance().registerAppUser(user);
    }
    Assert.assertEquals(10, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getUserQueue().size());
    Assert.assertEquals(10, CapacityHandler.getInstance().getNumberOfAssingedStations());
  }

  @Test
  public void exitUser_WithElevenUser_expectsEqual() {
    setupCapacityHandler(100, 10);

    AppUser user = new AppUser();
    CapacityHandler.getInstance().registerAppUser(user);
    for (int i = 0; i < 10; i++) {
      AppUser newUser = new AppUser();
      newUser.setId((long)i);
      CapacityHandler.getInstance().registerAppUser(newUser);
    }
    Assert.assertEquals(10, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getUserQueue().size());
    Assert.assertEquals(10, CapacityHandler.getInstance().getNumberOfAssingedStations());

    CapacityHandler.getInstance().exitUser(user);

    Assert.assertEquals(10, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(0, CapacityHandler.getInstance().getUserQueue().size());
    Assert.assertEquals(10, CapacityHandler.getInstance().getNumberOfAssingedStations());
  }

  @Test
  public void increaseCapacity_assertsEqual() {
    CapacityHandler.getInstance().registerAppUser(new AppUser());
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getNumberOfAssingedStations());
    CapacityHandler.getInstance().increaseWorkspaceCapacity(15);
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getNumberOfAssingedStations());
  }

  @Test
  public void setWorkplaceCapacity_withoutRestartDay_WithElevenUser_expectsEqual() {
    CapacityHandler.getInstance().setMaxWorkplaceSpace(100);
    CapacityHandler.getInstance().setWorkspaceCapacity(5);

    for (int i = 0; i < 11; i++) {
      AppUser user = new AppUser();
      user.setId((long)i);
      CapacityHandler.getInstance().registerAppUser(user);
    }

    Assert.assertEquals(11, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(0, CapacityHandler.getInstance().getUserQueue().size());
    Assert.assertEquals(11, CapacityHandler.getInstance().getNumberOfAssingedStations());
  }

  @Test
  public void enterUser_withRegisteredUser_assertsEqual() {
    AppUser user = new AppUser();
    CapacityHandler.getInstance().registerAppUser(user);
    Assert.assertTrue(CapacityHandler.getInstance().enterUser(user));
    Assert.assertEquals(1, CapacityHandler.getInstance().getUsersCurrentlyInOffice().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getNumberOfAssingedStations());
  }

  @Test
  public void enterUser_withoutRegistrationAndWithFreePlace_assertsEqual() {
    Assert.assertTrue(CapacityHandler.getInstance().enterUser(new AppUser()));
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getUsersCurrentlyInOffice().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getNumberOfAssingedStations());
  }

  @Test
  public void enterUser_withoutRegistrationAndWithNoFreePlace_assertsEqual() {
    setupCapacityHandler(10, 10);
    CapacityHandler.getInstance().registerAppUser(new AppUser());
    Assert.assertFalse(CapacityHandler.getInstance()
        .enterUser(new AppUser(1L, "asd", "asd", "asd", "asd", "asd", UserRole.EMPLOYEE)));
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(0, CapacityHandler.getInstance().getUsersCurrentlyInOffice().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getUserQueue().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getNumberOfAssingedStations());
  }

  @Test
  public void enterUserVIP_withNoFreePlace_assertsEqual() {
    Assert.assertTrue(CapacityHandler.getInstance().enterUser(new AppUser()));
    Assert.assertTrue(CapacityHandler.getInstance()
        .enterUser(new AppUser(1L, "asd", "asd", "asd", "asd", "asd", UserRole.VIP)));
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(2, CapacityHandler.getInstance().getUsersCurrentlyInOffice().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getNumberOfAssingedStations());
  }

  @Test
  public void getNthUserInQueue_withLessThanNUsers_assertNull() {
    Queue<AppUser> queue = CapacityHandler.getInstance().getUserQueue();
    for (int i = 0; i < 5; i++) {
      AppUser user = new AppUser();
      user.setId((long)i);
      queue.add(user);
    }
    Assert.assertNull(CapacityHandler.getInstance().getNthUserInQueue(6));
  }

  @Test
  public void getNthUserInQueue_withExactlyNUsers_assertEquals() {
    Queue<AppUser> queue = CapacityHandler.getInstance().getUserQueue();
    for (int i = 0; i < 5; i++) {
      AppUser user = new AppUser();
      user.setId((long)i);
      queue.add(user);
    }
    Assert.assertEquals(4, CapacityHandler.getInstance().getNthUserInQueue(5).getId(), 0.0001);
  }

  @Test
  public void getNthUserInQueue_withMoreThanNUsers_assertEquals() {
    Queue<AppUser> queue = CapacityHandler.getInstance().getUserQueue();
    for (int i = 0; i < 5; i++) {
      AppUser user = new AppUser();
      user.setId((long)i);
      queue.add(user);
    }
    Assert.assertEquals(2, CapacityHandler.getInstance().getNthUserInQueue(3).getId(), 0.0001);
  }

  private void setupCapacityHandler(Integer maxWorkplaceSpace, Integer workspaceCapacity) {
    CapacityHandler.getInstance().setMaxWorkplaceSpace(maxWorkplaceSpace);
    CapacityHandler.getInstance().setWorkspaceCapacity(workspaceCapacity);
    CapacityHandler.getInstance().restartDay();
  }
}
