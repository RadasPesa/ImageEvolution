package osu.sofco;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MyPanel extends JPanel {

    private static BufferedImage image;
    static BufferedImage newImage;

    public MyPanel() {
    }

    public void paint(Graphics g) {

        super.paint(g);

        g.setColor(Color.BLACK);
        g.drawRect(39,19,201,201);
        g.drawRect(545,19,201,201);
        g.drawImage(image,40,20,this);
        g.drawImage(newImage,546,20,this);
    }

    public static void loadImage() {
        try {
            image = ImageIO.read(new File(MainWindow.path));
            if (image.getWidth() > 200 || image.getHeight() > 200) {
                image = image.getSubimage(0,0,200,200);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static BufferedImage getImage() {
        return image;
    }

    public static void setNewImage(BufferedImage newImage) {
        MyPanel.newImage = newImage;
    }
}
