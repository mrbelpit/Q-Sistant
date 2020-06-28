package accenture.demo.capacity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class WorkStation {
    private Rectangle rectangle;
    private WorkStationStatus status;
    private List<WorkStation> nearbyStations;

    public WorkStation(Rectangle rectangle) {
        this.rectangle = rectangle;
        status = WorkStationStatus.FREE;
        nearbyStations = new ArrayList<>();
    }

    private Point getCenter() {
        return new Point((int)rectangle.getCenterX(), (int)rectangle.getCenterY());
    }

    public double distanceInPixels(WorkStation other) {
        return getCenter().distance(other.getCenter());
    }

    public double distanceInMetres(WorkStation other) {
        return getCenter().distance(other.getCenter()) / 10.0;
    }

    public void addNearbyStation(WorkStation station) {
        nearbyStations.add(station);
    }

    public int getNumberOfFreeNearbyStations() {
        int count = 0;
        for (WorkStation station : nearbyStations) {
            count += WorkStationStatus.FREE.equals(station.getStatus()) ? 1 : 0;
        }
        return count;
    }

    public boolean isNearOccupiedOrReserved() {
        for (WorkStation station : nearbyStations) {
            if (WorkStationStatus.RESERVED.equals(station.getStatus())
                    || WorkStationStatus.OCCUPIED.equals(station.getStatus())) {
                return true;
            }
        }
        return false;
    }
}
