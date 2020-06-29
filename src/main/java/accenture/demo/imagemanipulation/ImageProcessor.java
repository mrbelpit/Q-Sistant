package accenture.demo.imagemanipulation;

import accenture.demo.capacity.WorkStation;
import accenture.demo.capacity.WorkStationStatus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageProcessor {

    private BufferedImage image;
    private Color[][] pixelColors;
    private int width;
    private int height;
    private byte[] noWorkStationImage;

    public ImageProcessor(String url) {
        try {
            URL imageUrl = new URL(url);
            image = ImageIO.read(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException("Office layout url: \"" + url + "\" is malformed or does not contain a usable image!");
        }
        loadNoWorkStationImage();
        processImage();
    }

    public ImageProcessor(File file) {
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException("Office layout file: \"" + file.getName() + "\" does not contain a usable image!");
        }
        loadNoWorkStationImage();
        processImage();
    }

    private void loadNoWorkStationImage() {
        noWorkStationImage = new byte[3];
        try {
            String pictureFilePath = System.getenv("ACCESS_DENIED_PICTURE_FILEPATH");
            File noWorkStationSpecifiedImageFile = new File(pictureFilePath);
            BufferedImage noWorkStationBufferedImage = ImageIO.read(noWorkStationSpecifiedImageFile);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ImageIO.write(noWorkStationBufferedImage, "jpg", bao);
            noWorkStationImage = bao.toByteArray();
        } catch (Exception e) {
            // No such file, blank response instead.
        }
    }

    private void processImage() {

        width = image.getWidth();
        height = image.getHeight();

        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        pixelColors = new Color[height][width];

        final int pixelLength = 3;
        for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
            int argb = 0;
            argb += -16777216; // 255 alpha
            argb += ((int) pixels[pixel] & 0xff); // blue
            argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
            argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red

            pixelColors[row][col] = new Color(argb, true);

            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }
    }

    public List<WorkStation> processStations() {
        List<WorkStation> stations = new ArrayList<>();

        List<List<Point>> pixelGroups = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (isStation(row, col) && !pixelAlreadyGrouped(row, col, pixelGroups)) {
                    pixelGroups.add(processPixel(row, col));
                }
            }
        }

        for (List<Point> pixelGroup : pixelGroups) {
            int minRow = pixelGroup.get(0).x;
            int minCol = pixelGroup.get(0).y;
            int maxRow = minRow;
            int maxCol = minCol;
            for (int i = 1; i < pixelGroup.size(); i++) {
                int currentPixelRow = pixelGroup.get(i).x;
                int currentPixelCol = pixelGroup.get(i).y;
                minRow = currentPixelRow < minRow ? currentPixelRow : minRow;
                minCol = currentPixelCol < minCol ? currentPixelCol : minCol;
                maxRow = currentPixelRow > maxRow ? currentPixelRow : maxRow;
                maxCol = currentPixelCol > maxCol ? currentPixelCol : maxCol;
            }
            Rectangle stationRectangle = new Rectangle(minCol, minRow, maxCol - minCol + 1, maxRow - minRow + 1);
            stations.add(new WorkStation(stationRectangle));
        }
        return stations;
    }

    private boolean pixelAlreadyGrouped(int row, int col, List<List<Point>> pixelGroups) {
        Point pixel = new Point(row, col);
        for (List<Point> group : pixelGroups) {
            if (group.contains(pixel)) {
                return true;
            }
        }
        return false;
    }

    private List<Point> processPixel(int pixelRow, int pixelColumn) {
        List<Point> pointGroup = new ArrayList<>();

        ArrayDeque<Point> pointsToProcess = new ArrayDeque<>();
        pointsToProcess.add(new Point(pixelRow, pixelColumn));

        while (!pointsToProcess.isEmpty()) {
            Point p = pointsToProcess.removeFirst();
            if (!pointGroup.contains(p) && !pointsToProcess.contains(p)) {
                if (isStation(p)) {
                    pointGroup.add(p);
                    Point[] neighbors = new Point[] {
                            new Point(p.x, p.y - 1),
                            new Point(p.x, p.y + 1),
                            new Point(p.x - 1, p.y),
                            new Point(p.x + 1, p.y)};
                    pointsToProcess.addAll(Arrays.asList(neighbors));
                }
            }
        }
        return pointGroup;
    }

    private boolean isStation(Point p) {
        return isStation(p.x, p.y);
    }

    private boolean isStation(int row, int col) {
        return isStation(pixelColors[row][col]);
    }

    private boolean isStation(Color color) {
        return color.getRed() <= 5 && color.getGreen() <= 5 && color.getBlue() >= 250;
    }

    public byte[] getImageModifiedBySingleStation(WorkStation station) {
        BufferedImage result = getImageCopy();
        Graphics2D graph = result.createGraphics();
        Rectangle rectangle = new Rectangle(station.getRectangle());
        int rectangleWidth = (int) rectangle.getWidth();
        int rectangleHeight = (int) rectangle.getHeight();
        rectangle.setSize(rectangleWidth * 2, rectangleHeight * 2);
        rectangle.translate(-rectangleWidth / 2, -rectangleHeight / 2);
        graph.setColor(Color.RED);
        graph.fill(rectangle);
        graph.dispose();

        return getByteArrayFromImage(result);
    }

    public byte[] getImageModifiedByStations(List<WorkStation> stations) {
        BufferedImage result = getImageCopy();
        Graphics2D graph = result.createGraphics();
        for (WorkStation station : stations) {
            graph.setColor(getColorByStatus(station.getStatus()));
            graph.fill(station.getRectangle());
        }
        graph.dispose();

        return getByteArrayFromImage(result);
    }

    private byte[] getByteArrayFromImage(BufferedImage image) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", bao);
        } catch (IOException e) {
            // If the image is not usable, a RuntimeException will fire sooner
        }
        return bao.toByteArray();
    }

    private BufferedImage getImageCopy() {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(image.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private Color getColorByStatus(WorkStationStatus status) {
        switch (status) {
            case FREE:
                return Color.GREEN;
            case OCCUPIED:
                return Color.RED;
            case RESERVED:
                return Color.ORANGE;
            case UNAVAILABLE:
                return Color.PINK;
        }
        return Color.BLUE;
    }

    public byte[] getNoWorkStationImage() {
        return noWorkStationImage;
    }
}
