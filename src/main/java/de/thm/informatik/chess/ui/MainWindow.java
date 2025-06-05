package de.thm.informatik.chess.ui;

import javax.swing.JFrame;

public class MainWindow {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess");
        ChessPanel panel = new ChessPanel();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}
