package accenture.demo.unit.capacity;

import accenture.demo.capacity.CapacityHandler;
import accenture.demo.exception.capacity.CapacitySetupException;
import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.user.AppUser;
import java.util.ArrayList;
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
  }

  @Test
  public void addUser_expectsEqual() {
    CapacityHandler.getInstance().registerAppUser(new AppUser());
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
  }

  @Test
  public void addUser_withElevenUser_expectsEqual() {
    setupCapacityHandler(100, 10);

    for (int i = 0; i < 11; i++) {

      CapacityHandler.getInstance().registerAppUser(new AppUser());
    }
    Assert.assertEquals(10, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getUserQueue().size());
  }

  @Test
  public void exitUser_WithElevenUser_expectsEqual() {
    setupCapacityHandler(100, 10);

    AppUser user = new AppUser();
    CapacityHandler.getInstance().registerAppUser(user);
    for (int i = 0; i < 10; i++) {
      CapacityHandler.getInstance().registerAppUser(new AppUser());
    }
    Assert.assertEquals(10, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getUserQueue().size());

    CapacityHandler.getInstance().exitUser(user);

    Assert.assertEquals(10, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(0, CapacityHandler.getInstance().getUserQueue().size());
  }

  @Test
  public void increaseCapacity_assertsEqual() {
    CapacityHandler.getInstance().registerAppUser(new AppUser());
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
    CapacityHandler.getInstance().increaseWorkspaceCapacity(15);
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
  }

  @Test
  public void setWorkplaceCapacity_withoutRestartDay_WithElevenUser_expectsEqual() {
    CapacityHandler.getInstance().setMaxWorkplaceSpace(100);
    CapacityHandler.getInstance().setWorkspaceCapacity(5);

    for (int i = 0; i < 11; i++) {
      CapacityHandler.getInstance().registerAppUser(new AppUser());
    }

    Assert.assertEquals(11, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(0, CapacityHandler.getInstance().getUserQueue().size());
  }

  @Test
  public void enterUser_withRegisteredUser_assertsEqual() {
    AppUser user = new AppUser();
    CapacityHandler.getInstance().registerAppUser(user);
    Assert.assertTrue(CapacityHandler.getInstance().enterUser(user));
    Assert.assertEquals(1, CapacityHandler.getInstance().getUsersCurrentlyInOffice().size());
  }

  @Test
  public void enterUser_withoutRegistrationAndWithFreePlace_assertsEqual() {
    Assert.assertTrue(CapacityHandler.getInstance().enterUser(new AppUser()));
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getUsersCurrentlyInOffice().size());
  }

  @Test
  public void enterUser_withoutRegistrationAndWithNoFreePlace_assertsEqual() {
    setupCapacityHandler(10, 10);
    CapacityHandler.getInstance().registerAppUser(new AppUser());
    Assert.assertFalse(CapacityHandler.getInstance().enterUser(new AppUser(1L,"asd","asd","asd","asd","asd")));
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(0, CapacityHandler.getInstance().getUsersCurrentlyInOffice().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getUserQueue().size());
  }

  private void setupCapacityHandler(Integer maxWorkplaceSpace, Integer workspaceCapacity) {
    CapacityHandler.getInstance().setMaxWorkplaceSpace(maxWorkplaceSpace);
    CapacityHandler.getInstance().setWorkspaceCapacity(workspaceCapacity);
    CapacityHandler.getInstance().restartDay();
  }
}
