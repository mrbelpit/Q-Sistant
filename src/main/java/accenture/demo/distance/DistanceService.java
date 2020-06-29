package accenture.demo.distance;

import accenture.demo.capacity.Message;
import accenture.demo.exception.distance.DistanceException;

public interface DistanceService {

  Message setDistance(DistanceSetupDTO distanceSetupDTO) throws DistanceException;

  Integer getDistanceInMeter();

  Integer getDistanceInPixel();
}
