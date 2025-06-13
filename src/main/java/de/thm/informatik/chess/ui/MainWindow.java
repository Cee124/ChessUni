package de.thm.informatik.chess.ui;

import java.io.IOException;

import javax.swing.JFrame;

public class MainWindow {
    public static void main(String[] args) throws IOException {
        JFrame frameInput = new JFrame("Input Window");
        InputWindow input = new InputWindow();
        frameInput.setContentPane(input);

        frameInput.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameInput.setResizable(false);
        frameInput.setSize(400, 300);
        frameInput.setLocationRelativeTo(null);
        frameInput.setVisible(true);
    }
}