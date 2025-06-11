package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    private static final String MEDIA_PATH = "resource";

    public static String saveImage(BufferedImage image, String fileName) throws IOException {
        File mediaDir = new File(MEDIA_PATH);
        if (!mediaDir.exists()) {
            mediaDir.mkdirs();
        }

        String filePath = MEDIA_PATH + File.separator + fileName;
        File outputFile = new File(filePath);
        ImageIO.write(image, "jpg", outputFile);
        return fileName; // Возвращаем только имя файла
    }

    public static BufferedImage loadImage(String fileName) throws IOException {
        InputStream is = ImageUtils.class.getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new IOException("Изображение media/" + fileName + " не найдено");
        }
        return ImageIO.read(is);
    }

}