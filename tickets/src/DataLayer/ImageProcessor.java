package DataLayer;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessor {
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }
    public static ImageIcon getImageIcon (String picName, int x, int y) {
        String path = "img\\" + picName;
        File file = new File(path);
        BufferedImage image = null;
        ImageIcon imageIcon = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
        }

        BufferedImage resizedImage = resize(image, x, y);
        imageIcon = new ImageIcon(resizedImage);
        return imageIcon;
    }
}
