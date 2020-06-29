package accenture.demo.distance;

import accenture.demo.capacity.Message;
import accenture.demo.exception.distance.DistanceException;
import accenture.demo.exception.distance.UnitNotSupportedException;
import accenture.demo.exception.distance.ValueIsNotValidException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DistanceServiceImpl implements DistanceService {

  @Override
  public Message setDistance(DistanceSetupDTO distanceSetupDTO) throws DistanceException {
    checkDistanceSetup(distanceSetupDTO);

    switch (distanceSetupDTO.getUnit()) {
      case METER:
      case METRE:
        DistanceHandler.setDistanceInMeter(distanceSetupDTO.getValue());
        return new Message(
            "The distance was successfully set to " + distanceSetupDTO.getValue() + " "
                + distanceSetupDTO.getUnit().toString().toLowerCase()
                + ". It is valid from tomorrow.");
      default:
        return null;
    }
  }

  @Override
  public Integer getDistanceInMeter() {
    return DistanceHandler.getDistanceInMeter();
  }


  @Override
  public Integer getDistanceInPixel() {
    return DistanceHandler.getDistanceInPixel();
  }

  private void checkDistanceSetup(DistanceSetupDTO distanceSetupDTO) throws DistanceException {
    checkSetupUnit(distanceSetupDTO.getUnit());
    checkSetupValue(distanceSetupDTO.getValue());
  }

  private void checkSetupUnit(Unit unit) throws UnitNotSupportedException {
    List<Unit> supportedUnitList = Arrays.asList(Unit.values());
    if (!supportedUnitList.contains(unit)) {
      throw new UnitNotSupportedException(
          "The provided unit: " + unit.toString() + " is not supported.");
    }
  }

  private void checkSetupValue(Integer value) throws ValueIsNotValidException {
    if (value < 0) {
      throw new ValueIsNotValidException("The provided value is less than 0!");
    }
    if (value > 10) {
      throw new ValueIsNotValidException("The provided value is larger than 10!");
    }
  }
}
