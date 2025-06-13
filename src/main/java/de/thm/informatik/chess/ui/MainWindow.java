package de.thm.informatik.chess.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
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

        JFrame framePanel = new JFrame("Chess");
        ChessPanel panel = new ChessPanel();
        panel.addClock(5);
        framePanel.add(panel);

        // Bildschirmgröße holen und setzen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        framePanel.setSize(screenSize);
        framePanel.setLocation(0, 0);

        framePanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePanel.setVisible(true);
    }
}
