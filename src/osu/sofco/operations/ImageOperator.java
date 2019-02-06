package osu.sofco.operations;

import osu.sofco.MyPanel;

import java.awt.image.BufferedImage;

public abstract class ImageOperator {

    private static BufferedImage oldImage = MyPanel.getImage();
    private boolean initialized = false;

    public BufferedImage getNewImage() {
        BufferedImage ret;
        if (!initialized) {
            ret = new BufferedImage(200,200, BufferedImage.TYPE_INT_RGB);
            initialized = true;
        } else {
            ret = Evolution.getEvolvedImage();
        }

        doOperation(oldImage, ret);

        return ret;
    }

    protected abstract void doOperation(BufferedImage source, BufferedImage target);
}
