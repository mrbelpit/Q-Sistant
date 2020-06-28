package accenture.demo.distance;

public class DistanceHandler {

  private static Integer distanceInMeter = 5;

  public static Integer getDistanceInMeter() {
    return distanceInMeter;
  }

  public static void setDistanceInMeter(Integer distanceInMeter) {
    DistanceHandler.distanceInMeter = distanceInMeter;
  }

  public static Integer getDistanceInPixel() {
    return distanceInMeter * 10;
  }
}
