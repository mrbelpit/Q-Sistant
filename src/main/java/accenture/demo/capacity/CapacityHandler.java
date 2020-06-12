package accenture.demo.capacity;

import accenture.demo.user.User;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class CapacityHandler {

  private static CapacityHandler instance;
  private static Integer maxWorkplaceSpace = 250;
  private static Double workspaceCapacity = 0.1;
  private static Queue<User> allowedUsers = new LinkedBlockingQueue<>(
      (int) (maxWorkplaceSpace * workspaceCapacity));
  private static Queue<User> userQueue = new LinkedList<>();

  private CapacityHandler() {
  }

  public static CapacityHandler getInstance() {
    if (instance == null) {
      instance = new CapacityHandler();
    }
    return instance;
  }

  public static void addUser(User user) {
    if (!allowedUsers.offer(user)) {
      userQueue.add(user);
    }
  }

  public static void exitUser(User user) {
    allowedUsers.remove(user);
    User nextUser = userQueue.poll();
    allowedUsers.add(nextUser);
  }

  public static void restartDay() {
    allowedUsers = new LinkedBlockingQueue<>((int) (maxWorkplaceSpace * workspaceCapacity));
    userQueue = new LinkedList<>();
  }

  public static void setMaxWorkplaceSpace(Integer maxWorkplaceSpace) {
    CapacityHandler.maxWorkplaceSpace = maxWorkplaceSpace;
  }

  public static void setWorkspaceCapacity(Integer percentage) {
    CapacityHandler.workspaceCapacity = calculateNewWorkspaceCapacity(percentage);
  }

  public static void increaseWorkspaceCapacity(Integer percentage) {
    Double newWorkspaceCapacity = calculateNewWorkspaceCapacity(percentage);
    if (newWorkspaceCapacity > workspaceCapacity) {
      Queue<User> allowedUsersSaved = allowedUsers;
      Queue<User> userQueueSaved = userQueue;
      CapacityHandler.workspaceCapacity = newWorkspaceCapacity;
      restartDay();
      allowedUsers = allowedUsersSaved;
      userQueue = userQueueSaved;
    }
  }

  public static Queue<User> getAllowedUsers() {
    return allowedUsers;
  }

  public static Queue<User> getUserQueue() {
    return userQueue;
  }

  public static String currentPlaceInUserQueue(User user) {
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

  private static Double calculateNewWorkspaceCapacity(Integer percentage) {
    return Double.valueOf(percentage) / 100;
  }
}
