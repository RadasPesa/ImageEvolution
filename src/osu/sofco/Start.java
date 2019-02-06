package osu.sofco;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Start {

    private JFrame frame;
    private JPanel panel;
    private JLabel label;
    private JTextField textField;
    private JButton button;

    public Start() {
        frame = new JFrame("SOFCO");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400,200);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        label = new JLabel("Vlozte celou cestu k obrazku velikosti 200x200");
        label.setBounds(50,5,320,30);
        panel.add(label);

        textField = new JTextField();
        textField.setBounds(40,40,320,30);
        panel.add(textField);

        button = new JButton("Load");
        setButton(button);
        panel.add(button);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Start();
    }

    private void setButton(JButton button) {
        button.setBounds(150,100,80,30);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.path = textField.getText();
                frame.dispose();
                new MainWindow();
            }
        });
    }
}
