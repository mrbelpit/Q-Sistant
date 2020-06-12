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
    CapacityHandler.addUser(new User());
    Assert.assertEquals(1, CapacityHandler.getAllowedUsers().size());
  }

  @Test
  public void addUser_withElevenUser_expectEqual() {
    setupCapacityHandler(100, 10);

    for (int i = 0; i < 11; i++) {
      CapacityHandler.addUser(new User());
    }
    Assert.assertEquals(10, CapacityHandler.getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getUserQueue().size());
  }

  @Test
  public void exitUser_WithElevenUser_expectEqual() {
    setupCapacityHandler(100, 10);

    User user = new User();
    CapacityHandler.addUser(user);
    for (int i = 0; i < 10; i++) {
      CapacityHandler.addUser(new User());
    }
    Assert.assertEquals(10, CapacityHandler.getAllowedUsers().size());
    Assert.assertEquals(1, CapacityHandler.getUserQueue().size());

    CapacityHandler.exitUser(user);

    Assert.assertEquals(10, CapacityHandler.getAllowedUsers().size());
    Assert.assertEquals(0, CapacityHandler.getUserQueue().size());
  }

  @Test
  public void increaseCapacity_assertEqual() {
    CapacityHandler.addUser(new User());
    Assert.assertEquals(1, CapacityHandler.getAllowedUsers().size());
    CapacityHandler.increaseWorkspaceCapacity(15);
    Assert.assertEquals(1, CapacityHandler.getAllowedUsers().size());
  }


  @Test
  public void setWorkplaceCapacity_withoutRestartDay_WithElevenUser_expectEqual() {
    CapacityHandler.setMaxWorkplaceSpace(100);
    CapacityHandler.setWorkspaceCapacity(5);

    for (int i = 0; i < 11; i++) {
      CapacityHandler.addUser(new User());
    }

    Assert.assertEquals(11, CapacityHandler.getAllowedUsers().size());
    Assert.assertEquals(0, CapacityHandler.getUserQueue().size());
  }

  @Test
  public void currentPlaceInUserQueue_WithTenUser_expectEqual() {
    setupCapacityHandler(100, 5);

    for (int i = 0; i < 9; i++) {
      CapacityHandler.addUser(new User());
    }
    User user = new User();
    CapacityHandler.addUser(user);

    Assert.assertEquals(5, CapacityHandler.getAllowedUsers().size());
    Assert.assertEquals(5, CapacityHandler.getUserQueue().size());
    Assert.assertEquals("Your current place in the queue is " + 5,
        CapacityHandler.currentPlaceInUserQueue(user));
  }

  private void setupCapacityHandler(Integer maxWorkplaceSpace, Integer workspaceCapacity) {
    CapacityHandler.setMaxWorkplaceSpace(maxWorkplaceSpace);
    CapacityHandler.setWorkspaceCapacity(workspaceCapacity);
    CapacityHandler.restartDay();
  }
}
