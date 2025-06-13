package de.thm.informatik.chess.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;

public class MainWindow {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Chess");
        ChessPanel panel = new ChessPanel();
        panel.addClock(5);
        frame.add(panel);

        // Bildschirmgröße holen und setzen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize);
        frame.setLocation(0, 0);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
