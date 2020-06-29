package accenture.demo.unit.imagemanipulation;

import accenture.demo.imagemanipulation.ImageProcessor;
import accenture.demo.capacity.WorkStation;
import accenture.demo.capacity.WorkStationStatus;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ImageProcessorTest {
    ImageProcessor imageProcessor;
    List<WorkStation> stations;
    String prodUrl = "https://github.com/mrbelpit/Q-Sistant/blob/master/layout/accenture_layout.jpg?raw=true";

    @Before
    public void before() {
        imageProcessor = new ImageProcessor(prodUrl);
        stations = imageProcessor.processStations();
    }

    @Test
    public void processStations_withProdImage_assertEquals() {
        assertEquals(192, imageProcessor.processStations().size());
    }


    @Test
    public void processStations_withTwoRectangularStations_assertEquals() {
        File imageFile = new File("src/test/resources/two_rect.jpg");
        imageProcessor = new ImageProcessor(imageFile);
        assertEquals(2, imageProcessor.processStations().size());
    }

    @Test
    public void processStations_withBlankImage_assertEquals() {
        File imageFile = new File("src/test/resources/blank.jpg");
        imageProcessor = new ImageProcessor(imageFile);
        assertEquals(0, imageProcessor.processStations().size());
    }


    @Test
    public void processStations_withTwoConcaveStations_assertEquals() {
        File imageFile = new File("src/test/resources/three_concave.jpg");
        imageProcessor = new ImageProcessor(imageFile);
        assertEquals(3, imageProcessor.processStations().size());
    }

    @Test
    public void getImageModifiedByStations_afterModification_assertNotEquals() {
        byte[] imageBeforeFunction = imageProcessor.getImageModifiedByStations(stations);
        stations.get(0).setStatus(WorkStationStatus.OCCUPIED);
        byte[] imageAfterFunction = imageProcessor.getImageModifiedByStations(stations);
        assertNotEquals(imageBeforeFunction, imageAfterFunction);
    }

    @Test
    public void getImageModifiedBySingleStation_afterModification_assertNotEquals() {
        byte[] imageBeforeFunction = imageProcessor.getImageModifiedByStations(stations);
        byte[] imageAfterFunction = imageProcessor.getImageModifiedBySingleStation(stations.get(0));
        assertNotEquals(imageBeforeFunction, imageAfterFunction);
    }

    @Test(expected = RuntimeException.class)
    public void constructor_withMalformedURL_expectedRuntimeException() {
        imageProcessor = new ImageProcessor("https://iamafake.url");
    }


}
