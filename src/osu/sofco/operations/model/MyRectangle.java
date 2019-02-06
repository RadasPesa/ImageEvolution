package osu.sofco.operations.model;

import osu.sofco.MyPanel;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class MyRectangle {

    private Rectangle rectangle;
    private Color color;

    private double fitness;

    public static int mutationRate = 50;

    public MyRectangle(MyRectangle rect) {
        this.rectangle = rect.getRectangle();
        this.color = rect.getColor();
        this.fitness = rect.getFitness();
    }

    public MyRectangle(Color color) {
        int x = rand(MyPanel.getImage().getWidth() - 1);
        int y = rand(MyPanel.getImage().getHeight() - 1);
        int width = rand(MyPanel.getImage().getWidth() / 3);
        int height = rand(MyPanel.getImage().getHeight() / 3);
        this.rectangle = new Rectangle(x, y, width, height);
        this.color = color;
    }

    public MyRectangle(Double fitness) {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        this.fitness = fitness;

        this.rectangle = new Rectangle(x, y, width, height);
        this.color = new Color(0,0,0);
    }

    public MyRectangle() {
        int x = rand(MyPanel.getImage().getWidth() - 1);
        int y = rand(MyPanel.getImage().getHeight() - 1);
        int width = rand(MyPanel.getImage().getWidth() / 3);
        int height = rand(MyPanel.getImage().getHeight() / 3);

        this.rectangle = new Rectangle(x, y, width, height);
        this.color = new Color(rand(255), rand(255), rand(255), rand(255));
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Color getColor() {
        return color;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    private int rand(int max) {
        return ThreadLocalRandom.current().nextInt(0, max + 1);
    }

    public boolean mutate() {
        if (rand(100) < mutationRate) {
            int random = rand(4);
            switch (random) {
                case 0:
                    // mutate x coordinate (from 0 to the image's width - 1)
                    rectangle.x = rand(MyPanel.getImage().getWidth() - 1);
                    break;
                case 1:
                    // mutate y coordinate (from 0 to the image's height - 1)
                    rectangle.y = rand(MyPanel.getImage().getHeight() - 1);
                    break;
                case 2:
                    // mutate width of rectangle (from 0 to a third of the image's width)
                    rectangle.width = rand(MyPanel.getImage().getWidth() / 3);
                    break;
                case 3:
                    // mutate height of rectangle (from 0 to a third of the image's height)
                    rectangle.height = rand(MyPanel.getImage().getHeight() / 3);
                    break;
                case 4:
                    // mutate color of rectangle (rgba from 0 to 255)
                    color = new Color(rand(255), rand(255), rand(255), rand(255));
                    break;
                default:
                    System.out.println("Shouldn't happen!");
                    break;
            }
            return true;
        } else return false;
    }

    public void pass(MyRectangle parent, int genomePosition) {
        switch (genomePosition) {
            case 0:
                this.rectangle.x = (int) parent.getRectangle().getX();
            case 1:
                this.rectangle.y = (int) parent.getRectangle().getY();
            case 2:
                this.rectangle.width = (int) parent.getRectangle().getWidth();
            case 3:
                this.rectangle.height = (int) parent.getRectangle().getHeight();
            case 4:
                this.color = parent.getColor();
        }
    }
}
