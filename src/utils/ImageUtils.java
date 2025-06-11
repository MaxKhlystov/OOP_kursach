package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {
    private static final String MEDIA_PATH = "media";

    public static String saveImage(BufferedImage image, String fileName) throws IOException {
        File mediaDir = new File(MEDIA_PATH);
        if (!mediaDir.exists()) {
            boolean created = mediaDir.mkdirs();
            if (!created) {
                throw new IOException("Не удалось создать папку: " + MEDIA_PATH);
            }
        }

        File outputFile = new File(mediaDir, fileName);
        ImageIO.write(image, "jpg", outputFile);
        return outputFile.getAbsolutePath();
    }

    public static BufferedImage loadImage(String fileName) throws IOException {
        File imageFile = new File(MEDIA_PATH, fileName);
        if (!imageFile.exists()) {
            throw new IOException("Файл не найден: " + imageFile.getAbsolutePath());
        }
        return ImageIO.read(imageFile);
    }
}
