package accenture.demo.unit.distance;

import accenture.demo.capacity.Message;
import accenture.demo.distance.DistanceServiceImpl;
import accenture.demo.distance.DistanceSetupDTO;
import accenture.demo.distance.Unit;
import accenture.demo.exception.distance.DistanceException;
import accenture.demo.exception.distance.UnitNotSupportedException;
import accenture.demo.exception.distance.ValueIsNotValidException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DistanceServiceImplTest {

  private DistanceServiceImpl distanceService;

  @Before
  public void setup() {
distanceService = new DistanceServiceImpl();
  }

  @Test
  public void setDistance_withValidUnitMeterAndValue_assertsEqual() throws DistanceException {
   Message message = distanceService.setDistance(new DistanceSetupDTO(Unit.METER,3));
    String expectedMsg = "The distance was successfully set to 3 meter. It is valid from tomorrow.";
    Assert.assertEquals(expectedMsg, message.getMessage());
  }

  @Test
  public void setDistance_withValidUnitMetreAndValue_assertsEqual() throws DistanceException {
    Message message = distanceService.setDistance(new DistanceSetupDTO(Unit.METRE,3));
    String expectedMsg = "The distance was successfully set to 3 metre. It is valid from tomorrow.";
    Assert.assertEquals(expectedMsg, message.getMessage());
  }

  @Test(expected = ValueIsNotValidException.class)
  public void setDistance_withNegativeValue() throws DistanceException {
    distanceService.setDistance(new DistanceSetupDTO(Unit.METER,-1));
  }

  @Test(expected = ValueIsNotValidException.class)
  public void setDistance_withBigValue() throws DistanceException {
    distanceService.setDistance(new DistanceSetupDTO(Unit.METER,100));
  }
}
