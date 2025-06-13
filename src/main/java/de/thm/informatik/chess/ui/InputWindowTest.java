package de.thm.informatik.chess.ui;

import javax.swing.JFrame;

public class InputWindowTest extends JFrame{
    public static void main(String[] args) {
        JFrame frame = new JFrame("Input Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);    
        frame.setLocationRelativeTo(null);

        InputWindow input = new InputWindow();
        frame.setContentPane(input);
        frame.setResizable(false);

        frame.setVisible(true);
    }
}
