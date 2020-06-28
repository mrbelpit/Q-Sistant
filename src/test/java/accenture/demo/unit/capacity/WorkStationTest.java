package accenture.demo.unit.capacity;

import accenture.demo.capacity.WorkStation;
import accenture.demo.capacity.WorkStationStatus;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class WorkStationTest {
    private WorkStation station1;
    private WorkStation stationNear1A;
    private WorkStation stationNear1B;
    private WorkStation station2;

    @Before
    public void setup() {
        station1 = new WorkStation(new Rectangle(0, 0, 20, 20));
        stationNear1A = new WorkStation(new Rectangle(30, 0, 20, 20));
        stationNear1B = new WorkStation(new Rectangle(-20, 0, 20, 20));
        station1.addNearbyStation(stationNear1A);
        station1.addNearbyStation(stationNear1B);
        station2 = new WorkStation(new Rectangle(100, 0, 20, 20));
    }

    @Test
    public void getDistanceInMeters_assertEquals() {
        assertEquals(3, station1.distanceInMetres(stationNear1A), 0.0001f);
        assertEquals(2, station1.distanceInMetres(stationNear1B), 0.0001f);
        assertEquals(10, station1.distanceInMetres(station2), 0.0001f);
    }

    @Test
    public void getDistanceInPixels_assertEquals() {
        assertEquals(30, station1.distanceInPixels(stationNear1A), 0.0001f);
        assertEquals(20, station1.distanceInPixels(stationNear1B), 0.0001f);
        assertEquals(100, station1.distanceInPixels(station2), 0.0001f);
    }

    @Test
    public void getNumberOfFreeNearbyStations_withOneFree_assertEquals() {
        stationNear1A.setStatus(WorkStationStatus.OCCUPIED);
        assertEquals(1, station1.getNumberOfFreeNearbyStations());
    }

    @Test
    public void getNumberOfFreeNearbyStations_withNoNeighbors_assertEquals() {
        assertEquals(0, station2.getNumberOfFreeNearbyStations());
    }

    @Test
    public void isNearOccupiedOrReserved_withOneOccupied_assertTrue() {
        stationNear1A.setStatus(WorkStationStatus.OCCUPIED);
        assertTrue(station1.isNearOccupiedOrReserved());
    }

    @Test
    public void isNearOccupiedOrReserved_withNoNeighbors_assertFalse() {
        assertFalse(station2.isNearOccupiedOrReserved());
    }
}
