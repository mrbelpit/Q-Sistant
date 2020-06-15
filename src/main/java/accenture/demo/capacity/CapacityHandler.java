package accenture.demo.capacity;

import accenture.demo.exception.entry.EntryDeniedException;
import accenture.demo.user.User;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class CapacityHandler {

  private static CapacityHandler instance;
  private Integer maxWorkplaceSpace = 250;
  private Double workspaceCapacity = 0.1;
  private Queue<User> allowedUsers = new LinkedBlockingQueue<>(
      (int) (maxWorkplaceSpace * workspaceCapacity));
  private Queue<User> userQueue = new LinkedList<>();

  private CapacityHandler() {
  }

  public static CapacityHandler getInstance() {
    if (instance == null) {
      instance = new CapacityHandler();
    }
    return instance;
  }

  public void addUser(User user) {
    if (!allowedUsers.offer(user)) {
      userQueue.add(user);
    }
  }

  public void exitUser(User user) {
    allowedUsers.remove(user);
    User nextUser = userQueue.poll();
    allowedUsers.add(nextUser);
  }

  public void checkUserAllowed(User user) throws EntryDeniedException {
    if (!allowedUsers.contains(user)) {
      throw new EntryDeniedException("User is currently not allowed to enter!");
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

  public void increaseWorkspaceCapacity(Integer percentage) {
    Double newWorkspaceCapacity = calculateNewWorkspaceCapacity(percentage);
    if (newWorkspaceCapacity > workspaceCapacity) {
      Queue<User> allowedUsersSaved = allowedUsers;
      Queue<User> userQueueSaved = userQueue;
      CapacityHandler.getInstance().workspaceCapacity = newWorkspaceCapacity;
      restartDay();
      allowedUsers = allowedUsersSaved;
      userQueue = userQueueSaved;
    }
  }

  public Queue<User> getAllowedUsers() {
    return allowedUsers;
  }

  public Queue<User> getUserQueue() {
    return userQueue;
  }

  public String currentPlaceInUserQueue(User user) {
    for (int i = 0; i < userQueue.size(); i++) {
      if (userQueue.toArray()[i] == user) {
        return "Your current place in the queue is " + (i + 1);
      }
    }
    if (allowedUsers.contains(user)){
      return "You can enter the building today!";
    }
    return "You have not applied place to the office today!";
  }

  private Double calculateNewWorkspaceCapacity(Integer percentage) {
    return Double.valueOf(percentage) / 100;
  }
}
