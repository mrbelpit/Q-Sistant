package accenture.demo.capacity;

import accenture.demo.user.AppUser;
import accenture.demo.user.UserRole;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class CapacityHandler {

  private static CapacityHandler instance;
  private Integer maxWorkplaceSpace = 250;
  private Double workspaceCapacity = 0.1;
  private LinkedBlockingQueue<AppUser> allowedUsers = new LinkedBlockingQueue<>(
          (int) (maxWorkplaceSpace * workspaceCapacity));
  private Queue<AppUser> userQueue = new LinkedList<>();
  private ArrayList<AppUser> usersCurrentlyInOffice = new ArrayList<>();
  private int queuePlaceToSendNotificationTo = 3;

  private CapacityHandler() {
  }

  public static CapacityHandler getInstance() {
    if (instance == null) {
      instance = new CapacityHandler();
    }
    return instance;
  }

  public String registerAppUser(AppUser user) {
    if (!allowedUsers.offer(user)) {
      userQueue.add(user);
      return "Your current place in the queue " + userQueue.size()
             + "!";
    }
    return "You can enter the office!";
  }

  public boolean enterUser(AppUser user) {
    if (user.getUserRole() == UserRole.VIP) {
      usersCurrentlyInOffice.add(user);
      return true;
    } else if (allowedUsers.contains(user)) {
      usersCurrentlyInOffice.add(user);
      return true;
    } else if (allowedUsers.offer(user)) {
      usersCurrentlyInOffice.add(user);
      return true;
    } else {
      userQueue.add(user);
      return false;
    }
  }

  public void exitUser(AppUser user) {
    allowedUsers.remove(user);
    usersCurrentlyInOffice.remove(user);
    AppUser nextUser = userQueue.poll();
    if (nextUser != null) {
      allowedUsers.add(nextUser);
    }
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void restartDay() {
    allowedUsers = new LinkedBlockingQueue<>((int) (maxWorkplaceSpace * workspaceCapacity));
    userQueue = new LinkedList<>();
  }

  public void setMaxWorkplaceSpace(Integer maxWorkplaceSpace) {
    CapacityHandler.getInstance().maxWorkplaceSpace = maxWorkplaceSpace;
  }

  public void setWorkspaceCapacity(Integer percentage) {
    CapacityHandler.getInstance().workspaceCapacity = calculateNewWorkspaceCapacity(percentage);
  }

  public void setUsersCurrentlyInOffice(ArrayList<AppUser> usersCurrentlyInOffice) {
    this.usersCurrentlyInOffice = usersCurrentlyInOffice;
  }

  public void increaseWorkspaceCapacity(Integer percentage) {
    Double newWorkspaceCapacity = calculateNewWorkspaceCapacity(percentage);
    if (newWorkspaceCapacity > workspaceCapacity) {

      Queue<AppUser> allowedUsersSaved = allowedUsers;
      Queue<AppUser> userQueueSaved = userQueue;
      CapacityHandler.getInstance().workspaceCapacity = newWorkspaceCapacity;
      restartDay();
      allowedUsers.addAll(allowedUsersSaved);
      userQueue = userQueueSaved;
    }
  }

  public LinkedBlockingQueue<AppUser> getAllowedUsers() {
    return allowedUsers;
  }

  public Queue<AppUser> getUserQueue() {
    return userQueue;
  }

  public Integer getMaxWorkplaceSpace() {
    return maxWorkplaceSpace;
  }

  public Double getWorkspaceCapacity() {
    return workspaceCapacity;
  }

  public ArrayList<AppUser> getUsersCurrentlyInOffice() {
    return usersCurrentlyInOffice;
  }

  private Double calculateNewWorkspaceCapacity(Integer percentage) {
    return Double.valueOf(percentage) / 100;
  }

  public AppUser getNthUserInQueue(int n) {
    if (userQueue.size() == 0) {
      return null;
    }
    if (0 < n && n <= userQueue.size()) {
      ArrayList<AppUser> listOfUsersInQueue = new ArrayList(userQueue);
      return listOfUsersInQueue.get(n - 1);
    }
    return null;
  }

  public int getQueuePlaceToSendNotificationTo() {
    return queuePlaceToSendNotificationTo;
  }

  public void setQueuePlaceToSendNotificationTo(int queuePlaceToSendNotificationTo) {
    this.queuePlaceToSendNotificationTo = queuePlaceToSendNotificationTo;
  }
}
