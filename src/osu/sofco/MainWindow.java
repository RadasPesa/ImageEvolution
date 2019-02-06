package osu.sofco;

import osu.sofco.operations.Evolution;
import osu.sofco.operations.ImageOperator;
import osu.sofco.operations.model.MyRectangle;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainWindow {

    private JFrame frame;
    private MyPanel panel;
    private JButton runButton;
    private JButton stopButton;
    //private JButton saveButton;
    private JSlider mutationSlider;
    private JLabel mutationLabel;
    private JSpinner rectanglesSpinner;
    private SpinnerListModel spinnerModel;
    private JLabel populationLabel;
    private JLabel generationNumber;
    private JLabel fitnessLabel;
    private ImageOperator io;
    public static String path = "";

    private volatile boolean isRunning = false;

    public static int generationNo = 0;

    private AtomicBoolean paused;

    private final Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                if (paused.get()) {
                    synchronized (thread) {
                        try {
                            thread.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                increaseGeneration();
                MyPanel.setNewImage(io.getNewImage());
                fitnessLabel.setText("Best fitness: " + String.format("%.2f", Evolution.bestFitness) + "%");
                panel.repaint();
            }
        }
    });

    public MainWindow() {
        frame = new JFrame("SOFCO");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        panel = new MyPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        frame.add(panel);

        MyPanel.loadImage();

        paused = new AtomicBoolean(false);

        fitnessLabel = new JLabel("Best fitness: " + String.format("%.2f", Evolution.bestFitness) + "%");
        fitnessLabel.setBounds(318,210,170,25);
        fitnessLabel.setFont(new Font(fitnessLabel.getFont().getName(), Font.BOLD, 16));
        panel.add(fitnessLabel);

        generationNumber = new JLabel("Generation: " + generationNo);
        generationNumber.setBounds(335,150,250,25);
        generationNumber.setFont(new Font(generationNumber.getFont().getName(), Font.BOLD, 16));
        panel.add(generationNumber);

        Integer[] numberOfRectangles = new Integer[6];
        for (int j = 0; j < numberOfRectangles.length; j++) {
            numberOfRectangles[j] = (j + 1) * 25;
        }
        spinnerModel = new SpinnerListModel(numberOfRectangles);
        spinnerModel.setValue(Evolution.numberOfRectangles);
        rectanglesSpinner = new JSpinner(spinnerModel);
        rectanglesSpinner.setBounds(170,340,50,25);
        rectanglesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Evolution.numberOfRectangles = (int) rectanglesSpinner.getValue();
            }
        });
        panel.add(rectanglesSpinner);

        populationLabel = new JLabel("Population size: ");
        populationLabel.setBounds(75,340,150,25);
        panel.add(populationLabel);

        mutationSlider = new JSlider(1,100, MyRectangle.mutationRate);
        mutationSlider.setBounds(45,300,200,40);
        mutationSlider.setBackground(Color.WHITE);
        mutationSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                MyRectangle.mutationRate = mutationSlider.getValue();
                mutationLabel.setText("Mutation rate: " + MyRectangle.mutationRate + "%");
                panel.repaint();
            }
        });
        panel.add(mutationSlider);

        mutationLabel = new JLabel("Mutation rate: " + MyRectangle.mutationRate + "%");
        mutationLabel.setBounds(90,270,200,40);
        panel.add(mutationLabel);

        runButton = new JButton("START");
        runButton.setBounds(345,100,100,40);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isRunning) {
                    io = new Evolution();
                    isRunning = true;
                    runButton.setText("PAUSE");
                    thread.start();
                } else if (!paused.get()) {
                    runButton.setText("CONTINUE");
                    paused.set(true);
                } else if (paused.get()) {
                    runButton.setText("PAUSE");
                    paused.set(false);

                    synchronized (thread) {
                        thread.notify();
                    }
                }
            }
        });
        panel.add(runButton);

        stopButton = new JButton("EXIT");
        stopButton.setBounds(345,500,100,40);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isRunning = false;
                frame.dispose();
                System.exit(1);
            }
        });
        panel.add(stopButton);

        /*
        saveButton = new JButton("SAVE!");
        saveButton.setBounds(600,230,100,40);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ImageIO.write(MyPanel.newImage, "jpg", new File("K:\\Outputs\\img" + generationNo + ".jpg"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(saveButton);
        */

        panel.repaint();

        frame.setVisible(true);
    }

    private void increaseGeneration() {
        generationNo++;
        generationNumber.setText("Generation: " + generationNo);
    }

}