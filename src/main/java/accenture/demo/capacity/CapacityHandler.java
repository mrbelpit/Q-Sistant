package accenture.demo.capacity;

import accenture.demo.distance.DistanceHandler;
import accenture.demo.user.AppUser;
import accenture.demo.user.UserRole;
import accenture.demo.imagemanipulation.ImageProcessor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class CapacityHandler {

    private static CapacityHandler instance;
    private Integer maxWorkplaceSpace = 250;
    private Double workspaceCapacity = 0.1;
    private LinkedBlockingQueue<AppUser> allowedUsers;
    private Queue<AppUser> userQueue;
    private ArrayList<AppUser> usersCurrentlyInOffice = new ArrayList<>();
    private int queuePlaceToSendNotificationTo = 3;

    private List<WorkStation> stations;
    private Map<AppUser, WorkStation> assignedStations = new HashMap<>();
    private ImageProcessor imageProcessor;

    private CapacityHandler() {
        restartDay();
    }

    public static CapacityHandler getInstance() {
        if (instance == null) {
            instance = new CapacityHandler();
        }
        return instance;
    }

    public String registerAppUser(AppUser user) {
        if (getFreeWorkStationCount() == 0 || !allowedUsers.offer(user)) {
            userQueue.add(user);
            return "Your current place in the queue " + userQueue.size()
                    + "!";
        }
        reserveFreeWorkStationForUser(user);
        return "You can enter the office!";
    }

    public boolean enterUser(AppUser user) {
        if (user.getUserRole() == UserRole.VIP) {
            usersCurrentlyInOffice.add(user);
            return true;
        } else if (allowedUsers.contains(user)) {
            usersCurrentlyInOffice.add(user);
            assignedStations.get(user).setStatus(WorkStationStatus.OCCUPIED);
            return true;
        } else if (getFreeWorkStationCount() != 0 && allowedUsers.offer(user)) {
            usersCurrentlyInOffice.add(user);
            WorkStation assignecWorkStation = reserveFreeWorkStationForUser(user);
            assignecWorkStation.setStatus(WorkStationStatus.OCCUPIED);
            return true;
        } else {
            userQueue.add(user);
            return false;
        }
    }

    public void exitUser(AppUser user) {
        allowedUsers.remove(user);
        usersCurrentlyInOffice.remove(user);
        userExitedFromWorkStation(user);
        AppUser nextUser = userQueue.poll();
        if (nextUser != null) {
            allowedUsers.add(nextUser);
            reserveFreeWorkStationForUser(nextUser);
        }
    }

    private void userExitedFromWorkStation(AppUser user) {
        WorkStation station = assignedStations.remove(user);
        station.setStatus(WorkStationStatus.FREE);
        for (WorkStation neighbor : station.getNearbyStations()) {
            if (WorkStationStatus.UNAVAILABLE.equals(neighbor.getStatus()) && !neighbor.isNearOccupiedOrReserved()) {
                neighbor.setStatus(WorkStationStatus.FREE);
            }
        }
    }

    public void restartDay() {
        allowedUsers = new LinkedBlockingQueue<>((int) (maxWorkplaceSpace * workspaceCapacity));
        userQueue = new LinkedList<>();
        assignedStations = new HashMap<>();
        imageProcessor = new ImageProcessor(System.getenv("OFFICE_IMAGE_URL"));
        stations = imageProcessor.processStations();
        setNearbyStations();
    }

    private void setNearbyStations() {
        for (int i = 0; i < stations.size(); i++) {
            WorkStation station = stations.get(i);
            for (int j = 0; j < stations.size(); j++) {
                if (i != j) {
                    WorkStation nearbyStationCandidate = stations.get(j);
                    if (station.distanceInMetres(nearbyStationCandidate) < DistanceHandler.getDistanceInMeter()) {
                        station.addNearbyStation(nearbyStationCandidate);
                    }
                }
            }
        }
    }

    public WorkStation reserveFreeWorkStationForUser(AppUser user) {
        WorkStation stationWithLeastFreeNeighbors = null;
        for (WorkStation candidateStation : stations) {
            if (WorkStationStatus.FREE.equals(candidateStation.getStatus()) &&
                    (stationWithLeastFreeNeighbors == null ||
                            candidateStation.getNumberOfFreeNearbyStations() < stationWithLeastFreeNeighbors.getNumberOfFreeNearbyStations())) {
                stationWithLeastFreeNeighbors = candidateStation;
            }
        }
        if (stationWithLeastFreeNeighbors != null) {
            stationWithLeastFreeNeighbors.setStatus(WorkStationStatus.RESERVED);
            for (WorkStation neighbor : stationWithLeastFreeNeighbors.getNearbyStations()) {
                if (WorkStationStatus.FREE.equals(neighbor.getStatus())) {
                    neighbor.setStatus(WorkStationStatus.UNAVAILABLE);
                }
            }
        }
        assignedStations.put(user, stationWithLeastFreeNeighbors);
        return stationWithLeastFreeNeighbors;
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

    public void setAssignedStations(Map<AppUser, WorkStation> assignedStations) {
        this.assignedStations = assignedStations;
    }

    public void increaseWorkspaceCapacity(Integer percentage) {
        Double newWorkspaceCapacity = calculateNewWorkspaceCapacity(percentage);
        if (newWorkspaceCapacity > workspaceCapacity) {

            Queue<AppUser> allowedUsersSaved = allowedUsers;
            Queue<AppUser> userQueueSaved = userQueue;
            CapacityHandler.getInstance().workspaceCapacity = newWorkspaceCapacity;
            allowedUsers = new LinkedBlockingQueue<>((int) (maxWorkplaceSpace * workspaceCapacity));
            userQueue = new LinkedList<>();
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

    public int getNumberOfAssingedStations() {
        return assignedStations.size();
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

    private int getFreeWorkStationCount() {
        int count = 0;
        for (WorkStation station : stations) {
            if (WorkStationStatus.FREE.equals(station.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getQueuePlaceToSendNotificationTo() {
        return queuePlaceToSendNotificationTo;
    }

    public void setQueuePlaceToSendNotificationTo(int queuePlaceToSendNotificationTo) {
        this.queuePlaceToSendNotificationTo = queuePlaceToSendNotificationTo;
    }

    public byte[] getCurrentLayoutImage() {
        return imageProcessor.getImageModifiedByStations(stations);
    }

    public byte[] getAssignedStationImage(AppUser user) {
        WorkStation assignedStation = assignedStations.get(user);
        byte[] image;
        if (assignedStation != null) {
            image = imageProcessor.getImageModifiedBySingleStation(assignedStation);
        } else {
            image = imageProcessor.getNoWorkStationImage();
        }
        return image;
    }
}
