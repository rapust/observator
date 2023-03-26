package net.rapust.observator.commons.util;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.rapust.observator.commons.logger.MasterLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Image {

    private Robot robot;
    private Dimension screenSize;
    private Rectangle rectangle;

    @Setter
    private BufferedImage lastImage;

    public BufferedImage takeScreenShotImage() {
        return robot.createScreenCapture(rectangle);
    }

    public byte[] getChanges(BufferedImage newImage, byte[] newImageBytes) {
        List<Integer> changes = new ArrayList<>();

        for (int x = 0; x < newImage.getWidth(); x++) {
            for (int y = 0; y < newImage.getHeight(); y++) {

                int oldRgb = lastImage.getRGB(x, y);
                int newRgb = newImage.getRGB(x, y);

                if (oldRgb != newRgb) {
                    changes.add(x);
                    changes.add(y);
                    changes.add(newRgb);

                    if (changes.size() * 4 > newImageBytes.length) {
                        lastImage = newImage;
                        return null;
                    }
                }
            }
        }

        lastImage = newImage;

        int[] c = new int[changes.size()];
        int i = 0;

        for (Integer change : changes) {
            c[i] = change;
            i++;
        }

        return Bytes.convert(c);
    }

    public byte[] toByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, format, stream);
        return stream.toByteArray();
    }

    public BufferedImage toBufferedImage(byte[] bytes) throws IOException {
        InputStream stream = new ByteArrayInputStream(bytes);
        return ImageIO.read(stream);
    }

    public BufferedImage resizeImage(BufferedImage image, int width, int height) {
        java.awt.Image tmp = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = newImage.createGraphics();
        graphics.drawImage(tmp, 0, 0, null);
        graphics.dispose();

        return newImage;
    }

    static {
        try {
            robot = new Robot();
            screenSize = SystemInfo.getScreenInfo();
            rectangle = new Rectangle(screenSize);
        } catch (AWTException e) {
            MasterLogger.error(e);
        }
    }

}
