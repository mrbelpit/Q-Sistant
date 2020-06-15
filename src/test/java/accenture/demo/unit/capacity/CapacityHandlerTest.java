package accenture.demo.unit.capacity;

import accenture.demo.capacity.CapacityHandler;
import accenture.demo.user.User;
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
  }

  @Test
  public void addUser_expectEqual() {
    CapacityHandler.getInstance().addUser(new User());
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
  }

  @Test
  public void addUser_withElevenUser_expectEqual() {
    setupCapacityHandler(100, 10);

    for (int i = 0; i < 11; i++) {
      CapacityHandler.getInstance().addUser(new User());
    }
    Assert.assertEquals(10, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getUserQueue().size());
  }

  @Test
  public void exitUser_WithElevenUser_expectEqual() {
    setupCapacityHandler(100, 10);

    User user = new User();
    CapacityHandler.getInstance().addUser(user);
    for (int i = 0; i < 10; i++) {
      CapacityHandler.getInstance().addUser(new User());
    }
    Assert.assertEquals(10, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getInstance().getUserQueue().size());

    CapacityHandler.getInstance().exitUser(user);

    Assert.assertEquals(10, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(0, CapacityHandler.getInstance().getUserQueue().size());
  }

  @Test
  public void increaseCapacity_assertEqual() {
    CapacityHandler.getInstance().addUser(new User());
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
    CapacityHandler.getInstance().increaseWorkspaceCapacity(15);
    Assert.assertEquals(1, CapacityHandler.getInstance().getAllowedUsers().size());
  }


  @Test
  public void setWorkplaceCapacity_withoutRestartDay_WithElevenUser_expectEqual() {
    CapacityHandler.getInstance().setMaxWorkplaceSpace(100);
    CapacityHandler.getInstance().setWorkspaceCapacity(5);

    for (int i = 0; i < 11; i++) {
      CapacityHandler.getInstance().addUser(new User());
    }

    Assert.assertEquals(11, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(0, CapacityHandler.getInstance().getUserQueue().size());
  }

  @Test
  public void currentPlaceInUserQueue_WithTenUser_expectEqual() {
    setupCapacityHandler(100, 5);

    for (int i = 0; i < 9; i++) {
      CapacityHandler.getInstance().addUser(new User());
    }
    User user = new User();
    CapacityHandler.getInstance().addUser(user);

    Assert.assertEquals(5, CapacityHandler.getInstance().getAllowedUsers().size());
    Assert.assertEquals(5, CapacityHandler.getInstance().getUserQueue().size());
    Assert.assertEquals("Your current place in the queue is " + 5,
        CapacityHandler.getInstance().currentPlaceInUserQueue(user));
  }

  private void setupCapacityHandler(Integer maxWorkplaceSpace, Integer workspaceCapacity) {
    CapacityHandler.getInstance().setMaxWorkplaceSpace(maxWorkplaceSpace);
    CapacityHandler.getInstance().setWorkspaceCapacity(workspaceCapacity);
    CapacityHandler.getInstance().restartDay();
  }
}
