package osu.sofco.operations;


import osu.sofco.MainWindow;
import osu.sofco.operations.model.MyRectangle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Evolution extends ImageOperator {

    private Graphics2D g2d;
    private List<MyRectangle> generation;
    private static BufferedImage evolvedImage;
    private boolean genChanged = true;

    public static int numberOfRectangles = 100;
    public static double bestFitness = 0.0;

    public Evolution() {
        this.generation = new ArrayList<>();
        for (int i = 0; i < numberOfRectangles; i++) {
            generation.add(new MyRectangle());
        }
    }

    @Override
    protected void doOperation(BufferedImage source, BufferedImage target) {

        g2d = target.createGraphics();

        draw(g2d, source, target);

    }

    private double getDifference(BufferedImage source, BufferedImage target) {
        int width = source.getWidth();
        int height = source.getHeight();

        long difference = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                difference += pixelDifference(source.getRGB(i, j), target.getRGB(i, j));
            }
        }

        long maxDiff = 3L * 255 * width * height;

        return 100.0 * difference / maxDiff;
    }

    private int pixelDifference(int sourceRGB, int targetRGB) {
        int r1 = (sourceRGB >> 16) & 0xff;
        int g1 = (sourceRGB >> 8) & 0xff;
        int b1 = (sourceRGB) & 0xff;

        int r2 = (targetRGB >> 16) & 0xff;
        int g2 = (targetRGB >> 8) & 0xff;
        int b2 = (targetRGB) & 0xff;

        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

    private void draw(Graphics2D g2d, BufferedImage source, BufferedImage target) {
        g2d.setBackground(Color.black);

        // Paint generation
        if (genChanged) {
            g2d.clearRect(0,0,200,200);
            for (MyRectangle rect : generation) {
                fillRectangle(rect, g2d);
            }
        }

        // Crossover
        List<MyRectangle> newGeneration = new ArrayList<>();
        double totalSum = 0.0;
        double partialSum = 0.0;
        /*
        1. Spocitat fitness vsech obdelniku samotnych na platne oproti zdrojovemu obrazku
        1.1. ve foreachi si udelam novy prazdny obrazek
        1.2. vytvorim si k tomu obrazku graphics
        1.3. aktualni obdelnik do obrazku zakreslim
        1.4. spocitam diferenci obrazku od zdroje
        1.5. diferenci/fitness zaznacim do promenne v mem obdelniku
        2. Spocitam sumu vsech fitness
        2.1. foreach pres vsechny obdelniky a suma totalSum += getFitness()
        3. Vygeneruju nahodne cislo rand z intervalu <0, S>
        4. Prochazim celou populaci a scitam fitness partialSum od <0, S>
        4.1. Jakmile suma partialSum prekroci rand, zastavim a vratim obdelnik u ktereho jsem
        5. To same udelam pro druheho rodice
        6. Pokrizim oba rodice a potomka vlozim do nove generace
        7. Opakuju dokud nema nova generace velikost jako populace (numberOfRectangles)
         */
        for (MyRectangle rectangle : generation) {
            BufferedImage tmpImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D tmpG2D = tmpImg.createGraphics();
            fillRectangle(rectangle, tmpG2D);
            double fitness = 100.0 - getDifference(source, tmpImg);
            rectangle.setFitness(fitness);
            totalSum += fitness;
        }
        BufferedImage newGenImg = new BufferedImage(200,200, BufferedImage.TYPE_INT_RGB);
        Graphics2D newGenG2D = newGenImg.createGraphics();
        while (newGeneration.size() <= numberOfRectangles) {
            int rand = rand((int) totalSum);
            MyRectangle parent1 = null;
            MyRectangle parent2 = null;
            MyRectangle offspring;
            for (MyRectangle rectangle : generation) {
                partialSum += rectangle.getFitness();
                if (partialSum >= rand - 1) {
                    partialSum = 0.0;
                    parent1 = rectangle;
                    break;
                }
            }
            rand = rand((int) totalSum);
            for (MyRectangle rectangle : generation) {
                partialSum += rectangle.getFitness();
                if (partialSum >= rand - 1) {
                    parent2 = rectangle;
                    partialSum = 0.0;
                    break;

                }
            }
            offspring = crossover(parent1, parent2, source);
            newGeneration.add(offspring);
            fillRectangle(offspring, newGenG2D);
        }

        // Elitism in the form of generations instead of individuals
        if (getDifference(source, newGenImg) <= getDifference(source, target)) {
            generation.clear();
            generation.addAll(newGeneration);
            newGeneration.clear();
            genChanged = true;
        } else {
            genChanged = false;
            newGeneration.clear();
        }

        // Mutation
        for (MyRectangle rect : generation) {
            MyRectangle tmpRect = new MyRectangle(rect);
            boolean tmp = tmpRect.mutate();
            if (tmp) {
                BufferedImage tmpImg = copyImage(target);
                Graphics2D tmpG2D = tmpImg.createGraphics();
                //tmpG2D.clearRect(tmpRect.getRectangle().x, tmpRect.getRectangle().y, tmpRect.getRectangle().width, tmpRect.getRectangle().height);
                fillRectangle(tmpRect, tmpG2D);
                if (getDifference(source, tmpImg) <= getDifference(source, target)) {
                    //g2d.clearRect(tmpRect.getRectangle().x, tmpRect.getRectangle().y, tmpRect.getRectangle().width, tmpRect.getRectangle().height);
                    rect = tmpRect;
                    fillRectangle(rect, g2d);
                }
            }
        }
        // Save every 100th generation
        /*
        if (((MainWindow.generationNo % 100) == 0) || MainWindow.generationNo == 1) {
            try {
                ImageIO.write(target, "jpg", new File("images/ret" + MainWindow.generationNo + "-" + bestFitness + ".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
        bestFitness = 100.0 - getDifference(source, target);
        evolvedImage = copyImage(target);

        g2d.dispose();
    }

    private void fillRectangle(MyRectangle rect, Graphics2D g2d) {
        g2d.setColor(rect.getColor());
        g2d.fill(rect.getRectangle());
    }

    private static BufferedImage copyImage(BufferedImage copy) {
        ColorModel cm = copy.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = copy.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    static BufferedImage getEvolvedImage() {
        return evolvedImage;
    }

    private int rand(int max) {
        return ThreadLocalRandom.current().nextInt(0, max + 1);
    }

    private MyRectangle crossover(MyRectangle parent1, MyRectangle parent2, BufferedImage source) {
        MyRectangle offspring = new MyRectangle(0.0);
        int crossoverPoint = rand(3);

        for (int i = 0; i < crossoverPoint + 1; i++) {
            offspring.pass(parent1, i);
        }
        for (int i = crossoverPoint + 1; i <= 4; i++) {
            offspring.pass(parent2, i);
        }

        return offspring;
    }
}
