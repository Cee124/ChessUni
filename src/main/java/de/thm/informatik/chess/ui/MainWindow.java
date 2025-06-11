package de.thm.informatik.chess.ui;

import java.io.IOException;

import javax.swing.JFrame;

public class MainWindow {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Chess");
        ChessPanel panel = new ChessPanel();
        panel.addClock(5);
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}
